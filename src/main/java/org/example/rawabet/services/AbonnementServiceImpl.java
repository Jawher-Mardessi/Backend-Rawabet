package org.example.rawabet.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.SubscribeResponse;
import org.example.rawabet.dto.SubscriptionDto;
import org.example.rawabet.dto.TimelineResponse;
import org.example.rawabet.dto.UserSubscriptionResponse;
import org.example.rawabet.entities.Abonnement;
import org.example.rawabet.entities.QRCode;
import org.example.rawabet.entities.User;
import org.example.rawabet.entities.UserAbonnement;
import org.example.rawabet.enums.ActionType;
import org.example.rawabet.enums.AbonnementType;
import org.example.rawabet.enums.SubscriptionStatus;
import org.example.rawabet.repositories.AbonnementRepository;
import org.example.rawabet.repositories.QRCodeRepository;
import org.example.rawabet.repositories.UserAbonnementRepository;
import org.example.rawabet.repositories.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AbonnementServiceImpl {

    private static final int UNLIMITED_TICKETS = 10000;

    private final AbonnementRepository abonnementRepository;
    private final UserAbonnementRepository userAbonnementRepository;
    private final UserRepository userRepository;
    private final ICarteFideliteService carteFideliteService;
    private final QRCodeRepository qrCodeRepository;

    // ==================== Initialization ====================

    public void initAbonnements() {
        if (abonnementRepository.count() == 0) {
            abonnementRepository.save(
                    new Abonnement(null, AbonnementType.VIP, "VIP", 0, true, true, 100)
            );
            abonnementRepository.save(
                    new Abonnement(null, AbonnementType.Premium, "Premium", 5, false, true, 50)
            );
            abonnementRepository.save(
                    new Abonnement(null, AbonnementType.Standard, "Standard", 2, false, true, 20)
            );
        }
    }

    public List<UserAbonnement> getAllAbonnements() {
        return userAbonnementRepository.findAll();
    }

    public List<SubscriptionDto> getAllSubscriptions() {
        return userAbonnementRepository.findAll().stream()
                .map(this::mapToSubscriptionDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteSubscriptionById(Long subscriptionId) {
        UserAbonnement subscription = userAbonnementRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        qrCodeRepository.deleteByUserAbonnementId(subscriptionId);
        userAbonnementRepository.delete(subscription);
    }

    // ==================== Subscription Management ====================

    /**
     * Subscribe a user to an abonnement.
     * If no non-expired subscription exists, subscription starts TODAY (ACTIVATED_NOW).
     * Otherwise, subscription is QUEUED_NEXT, starting the day after previous subscription ends.
     */
    @Transactional
    public SubscribeResponse subscribe(Long userId, Long abonnementId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Abonnement abonnement = abonnementRepository.findById(abonnementId)
                .orElseThrow(() -> new RuntimeException("Abonnement not found"));

        // Read all existing subscriptions for this user (avoid overlap)
        List<UserAbonnement> existingSubscriptions = userAbonnementRepository.findByUserIdOrderByDateDebutAsc(userId);

        // Compute dates for the new subscription
        LocalDate today = LocalDate.now();
        LocalDate newDateDebut;
        LocalDate newDateFin;
        String resultType;

        // Check if there are any active or future subscriptions
        Optional<UserAbonnement> futureSubscription = existingSubscriptions.stream()
                .filter(ua -> ua.getDateDebut().isAfter(today) || (ua.getDateDebut().isBefore(today) || ua.getDateDebut().isEqual(today))
                        && ua.getDateFin().isAfter(today) || ua.getDateFin().isEqual(today))
                .findFirst();

        if (futureSubscription.isEmpty()) {
            // No active/future subscription: start TODAY
            newDateDebut = today;
            resultType = "ACTIVATED_NOW";
        } else {
            // There are active/future subscriptions: queue for the day after the last one ends
            LocalDate lastEndDate = existingSubscriptions.stream()
                    .map(UserAbonnement::getDateFin)
                    .max(LocalDate::compareTo)
                    .orElse(today);
            newDateDebut = lastEndDate.plusDays(1);
            resultType = "QUEUED_NEXT";
        }

        // Compute dateFin based on subscription duration (1 month)
        newDateFin = newDateDebut.plusMonths(1).minusDays(1);

        // Create new subscription
        UserAbonnement newSubscription = new UserAbonnement();
        newSubscription.setUser(user);
        newSubscription.setAbonnement(abonnement);
        newSubscription.setTicketsRestants(resolveTicketsRestants(abonnement));
        newSubscription.setDateDebut(newDateDebut);
        newSubscription.setDateFin(newDateFin);

        // Set status based on resultType
        SubscriptionStatus status = resultType.equals("ACTIVATED_NOW") ? SubscriptionStatus.ACTIVE : SubscriptionStatus.QUEUED;
        newSubscription.setStatus(status);

        UserAbonnement savedSubscription = userAbonnementRepository.save(newSubscription);

        // Award fidelity points only for immediately activated subscriptions
        if (status == SubscriptionStatus.ACTIVE) {
            carteFideliteService.getCarteByUser(user);
            int fidelityPoints = getSubscriptionFidelityPoints(abonnement.getType());
            if (fidelityPoints > 0) {
                carteFideliteService.addPoints(user, fidelityPoints, ActionType.BONUS);
            }
        }

        // Generate QR code
        generateQRCode(savedSubscription);

        // Build response
        return SubscribeResponse.builder()
                .userId(userId)
                .subscriptionId(savedSubscription.getId())
                .abonnementId(abonnement.getId())
                .abonnementType(abonnement.getType().name())
                .dateDebut(newDateDebut)
                .dateFin(newDateFin)
                .ticketsRestants(savedSubscription.getTicketsRestants())
                .status(status)
                .resultType(resultType)
                .message(resultType.equals("ACTIVATED_NOW")
                        ? "Subscription activated now"
                        : "Subscription queued and will start on " + newDateDebut)
                .build();
    }

    // ==================== Status Management ====================

    /**
     * Compute subscription status based on date and tickets.
     */
    public SubscriptionStatus computeStatus(UserAbonnement subscription) {
        LocalDate today = LocalDate.now();

        if (subscription.getDateDebut().isAfter(today)) {
            return SubscriptionStatus.QUEUED;
        }

        if (subscription.getDateFin().isBefore(today)) {
            return SubscriptionStatus.EXPIRED;
        }

        // Today is within dateDebut and dateFin
        // Check for exhausted status (tickets = 0 but still within valid dates)
        if (!subscription.getAbonnement().isIllimite() && subscription.getTicketsRestants() <= 0) {
            return SubscriptionStatus.EXHAUSTED;
        }

        return SubscriptionStatus.ACTIVE;
    }

    /**
     * Update status of all user subscriptions based on current logic.
     */
    @Transactional
    public void updateSubscriptionStatuses(Long userId) {
        List<UserAbonnement> subscriptions = userAbonnementRepository.findByUserIdOrderByDateDebutAsc(userId);
        for (UserAbonnement subscription : subscriptions) {
            SubscriptionStatus newStatus = computeStatus(subscription);
            if (!newStatus.equals(subscription.getStatus())) {
                subscription.setStatus(newStatus);
                userAbonnementRepository.save(subscription);
            }
        }
    }

    /**
     * Scheduled job to update statuses of all subscriptions.
     * Can be called daily to ensure fresh status without relying on subscription access.
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void refreshAllSubscriptionStatuses() {
        List<UserAbonnement> allSubscriptions = userAbonnementRepository.findAll();
        for (UserAbonnement subscription : allSubscriptions) {
            SubscriptionStatus newStatus = computeStatus(subscription);
            if (!newStatus.equals(subscription.getStatus())) {
                subscription.setStatus(newStatus);
            }
        }
    }

    // ==================== Timeline Endpoint ====================

    /**
     * Get comprehensive timeline for a user:
     * - currentSubscription: active subscription (null if none)
     * - nextSubscription: first QUEUED subscription (null if none)
     * - queuedSubscriptions: all QUEUED subscriptions sorted by dateDebut asc
     * - history: all EXPIRED subscriptions sorted by dateDebut desc
     */
    public TimelineResponse getTimelineForUser(Long userId) {
        updateSubscriptionStatuses(userId);

        List<UserAbonnement> allSubscriptions = userAbonnementRepository.findByUserIdOrderByDateDebutAsc(userId);

        SubscriptionDto currentSubscription = null;
        SubscriptionDto nextSubscription = null;
        List<SubscriptionDto> queuedSubscriptions = allSubscriptions.stream()
                .filter(ua -> ua.getStatus() == SubscriptionStatus.QUEUED)
                .map(this::mapToSubscriptionDto)
                .collect(Collectors.toList());

        // Find current subscription
        Optional<UserAbonnement> current = allSubscriptions.stream()
                .filter(ua -> ua.getStatus() == SubscriptionStatus.ACTIVE)
                .findFirst();
        if (current.isPresent()) {
            currentSubscription = mapToSubscriptionDto(current.get());
        }

        // Next subscription is the first queued
        if (!queuedSubscriptions.isEmpty()) {
            nextSubscription = queuedSubscriptions.get(0);
        }

        // History includes expired and exhausted (sorted by dateDebut desc)
        List<SubscriptionDto> history = allSubscriptions.stream()
                .filter(ua -> ua.getStatus() == SubscriptionStatus.EXPIRED || ua.getStatus() == SubscriptionStatus.EXHAUSTED)
                .sorted((a, b) -> b.getDateDebut().compareTo(a.getDateDebut()))
                .map(this::mapToSubscriptionDto)
                .collect(Collectors.toList());

        return TimelineResponse.builder()
                .userId(userId)
                .currentSubscription(currentSubscription)
                .nextSubscription(nextSubscription)
                .queuedSubscriptions(queuedSubscriptions)
                .history(history)
                .build();
    }

    // ==================== User Subscriptions ====================

    /**
     * Get all subscriptions for a user, sorted by dateDebut ascending.
     */
    public List<SubscriptionDto> getUserSubscriptions(Long userId) {
        updateSubscriptionStatuses(userId);
        List<UserAbonnement> subscriptions = userAbonnementRepository.findByUserIdOrderByDateDebutAsc(userId);
        return subscriptions.stream()
                .map(this::mapToSubscriptionDto)
                .collect(Collectors.toList());
    }

    /**
     * Get plain abonnements linked to a user.
     */
    public List<Abonnement> getAbonnementsByUserId(Long userId) {
        return userAbonnementRepository.findByUserIdOrderByDateDebutAsc(userId).stream()
                .map(UserAbonnement::getAbonnement)
                .collect(Collectors.toList());
    }

    // ==================== QR Code Management ====================

    @Transactional
    public void generateQRCode(UserAbonnement userAbonnement) {
        QRCode qrCode = qrCodeRepository.findByUserAbonnementId(userAbonnement.getId())
                .orElseGet(QRCode::new);

        qrCode.setCode(UUID.randomUUID().toString());
        qrCode.setUserAbonnement(userAbonnement);
        qrCode.setUsed(false);
        qrCode.setCreatedAt(LocalDateTime.now());
        qrCode.setScannedAt(null);

        qrCodeRepository.save(qrCode);
    }

    @Transactional
    public ScanQRResponse scanQRCode(String qrCodeString) {
        QRCode qrCode = qrCodeRepository.findByCode(qrCodeString)
                .orElseThrow(() -> new RuntimeException("QR Code not found"));

        if (qrCode.isUsed()) {
            throw new RuntimeException("QR Code already used");
        }

        UserAbonnement userAbonnement = qrCode.getUserAbonnement();

        // Verify subscription is ACTIVE
        SubscriptionStatus status = computeStatus(userAbonnement);
        if (status != SubscriptionStatus.ACTIVE) {
            if (status == SubscriptionStatus.QUEUED) {
                throw new RuntimeException("Subscription not active yet, starts on " + userAbonnement.getDateDebut());
            }
            throw new RuntimeException("Subscription not active (status: " + status + ")");
        }

        if (userAbonnement.getTicketsRestants() <= 0) {
            throw new RuntimeException("No tickets remaining");
        }

        userAbonnement.setTicketsRestants(userAbonnement.getTicketsRestants() - 1);
        userAbonnementRepository.save(userAbonnement);

        qrCode.setUsed(true);
        qrCode.setScannedAt(LocalDateTime.now());
        qrCodeRepository.save(qrCode);

        return new ScanQRResponse(
                "Ticket consumed successfully!",
                userAbonnement.getTicketsRestants(),
                userAbonnement.getUser().getNom()
        );
    }

    @Getter
    @AllArgsConstructor
    public static class ScanQRResponse {
        private String message;
        private int ticketsRemaining;
        private String userName;
    }

    public String getQRCodeByUserId(Long userId) {
        // Get the ACTIVE subscription only
        LocalDate today = LocalDate.now();
        List<UserAbonnement> subscriptions = userAbonnementRepository.findByUserIdOrderByDateDebutAsc(userId);

        UserAbonnement activeSubscription = subscriptions.stream()
                .filter(ua -> ua.getStatus() == SubscriptionStatus.ACTIVE
                        || (ua.getDateDebut().isBefore(today) || ua.getDateDebut().isEqual(today))
                           && (ua.getDateFin().isAfter(today) || ua.getDateFin().isEqual(today)))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No active subscription found for this user"));

        QRCode qrCode = qrCodeRepository.findByUserAbonnementId(activeSubscription.getId())
                .orElseThrow(() -> new RuntimeException("QR Code not found for this user"));

        return qrCode.getCode();
    }

    // ==================== Legacy Methods (Backward Compatibility) ====================


    /**
     * DEPRECATED: Use getTimelineForUser instead.
     * Returns the first active subscription only.
     */
    @Deprecated
    public List<UserAbonnement> getUserAbonnements() {
        return userAbonnementRepository.findAll();
    }

    /**
     * DEPRECATED: Use getTimelineForUser instead.
     * Returns only the current active subscription.
     */
    @Deprecated
    public UserSubscriptionResponse getSubscriptionByUserId(Long userId) {
        LocalDate today = LocalDate.now();
        List<UserAbonnement> subscriptions = userAbonnementRepository.findByUserIdOrderByDateDebutAsc(userId);

        UserAbonnement activeSubscription = subscriptions.stream()
                .filter(ua -> ua.getStatus() == SubscriptionStatus.ACTIVE
                        || (ua.getDateDebut().isBefore(today) || ua.getDateDebut().isEqual(today))
                           && (ua.getDateFin().isAfter(today) || ua.getDateFin().isEqual(today)))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Subscription not found for this user"));

        return mapToSubscriptionResponse(activeSubscription);
    }

    /**
     * DEPRECATED: No longer deletes subscriptions. Subscriptions are kept with EXPIRED status.
     */
    @Deprecated
    public long cleanupExpiredUserAbonnements() {
        // No-op: subscriptions are no longer deleted
        // Call updateSubscriptionStatuses for all users instead
        refreshAllSubscriptionStatuses();
        return 0;
    }

    // ==================== Helper Methods ====================

    private int getSubscriptionFidelityPoints(AbonnementType abonnementType) {
        if (abonnementType == AbonnementType.Premium) {
            return 2;
        }
        if (abonnementType == AbonnementType.VIP) {
            return 5;
        }
        return 0;
    }

    private int resolveTicketsRestants(Abonnement abonnement) {
        if (abonnement.getType() == AbonnementType.VIP) {
            return UNLIMITED_TICKETS;
        }
        if (abonnement.getType() == AbonnementType.Premium) {
            return 5;
        }
        if (abonnement.getType() == AbonnementType.Standard) {
            return 2;
        }
        return abonnement.isIllimite() ? UNLIMITED_TICKETS : abonnement.getNbTicketsParMois();
    }

    private UserSubscriptionResponse mapToSubscriptionResponse(UserAbonnement userAbonnement) {
        return UserSubscriptionResponse.builder()
                .userId(userAbonnement.getUser().getId())
                .userAbonnementId(userAbonnement.getId())
                .abonnementId(userAbonnement.getAbonnement().getId())
                .abonnementNom(userAbonnement.getAbonnement().getNom())
                .abonnementType(userAbonnement.getAbonnement().getType().name())
                .ticketsRestants(userAbonnement.getTicketsRestants())
                .dateDebut(userAbonnement.getDateDebut())
                .dateFin(userAbonnement.getDateFin())
                .build();
    }

    private SubscriptionDto mapToSubscriptionDto(UserAbonnement userAbonnement) {
        String qrCode = qrCodeRepository.findByUserAbonnementId(userAbonnement.getId())
                .map(QRCode::getCode)
                .orElse(null);

        return SubscriptionDto.builder()
                .subscriptionId(userAbonnement.getId())
                .abonnementId(userAbonnement.getAbonnement().getId())
                .abonnementType(userAbonnement.getAbonnement().getType().name())
                .abonnementName(userAbonnement.getAbonnement().getNom())
                .qrCode(qrCode)
                .dateDebut(userAbonnement.getDateDebut())
                .dateFin(userAbonnement.getDateFin())
                .ticketsRestants(userAbonnement.getTicketsRestants())
                .isIllimited(userAbonnement.getAbonnement().isIllimite())
                .status(userAbonnement.getStatus())
                .build();
    }
}
