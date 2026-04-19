package org.example.rawabet.services;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.abonnement.SubscribeResponseDTO;
import org.example.rawabet.dto.abonnement.SubscriptionTimelineDTO;
import org.example.rawabet.dto.abonnement.UserAbonnementDTO;
import org.example.rawabet.entities.Abonnement;
import org.example.rawabet.entities.QRCode;
import org.example.rawabet.entities.User;
import org.example.rawabet.entities.UserAbonnement;
import org.example.rawabet.enums.SubscribeResultType;
import org.example.rawabet.enums.UserAbonnementStatus;
import org.example.rawabet.repositories.AbonnementRepository;
import org.example.rawabet.repositories.QRCodeRepository;
import org.example.rawabet.repositories.UserAbonnementRepository;
import org.example.rawabet.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AbonnementServiceImpl implements IAbonnementService {

    private final AbonnementRepository abonnementRepository;
    private final UserAbonnementRepository userAbonnementRepository;
    private final QRCodeRepository qrCodeRepository;
    private final UserRepository userRepository;

    // ══════════════════════════════════════════════════════════════════════════
    // Plan (template) CRUD
    // ══════════════════════════════════════════════════════════════════════════

    @Override
    public Abonnement addAbonnement(Abonnement abonnement) {
        return abonnementRepository.save(abonnement);
    }

    @Override
    public Abonnement updateAbonnement(Abonnement abonnement) {
        if (!abonnementRepository.existsById(abonnement.getId())) {
            throw new RuntimeException("Plan introuvable : " + abonnement.getId());
        }
        return abonnementRepository.save(abonnement);
    }

    @Override
    public void deleteAbonnement(Long id) {
        if (!abonnementRepository.existsById(id)) {
            throw new RuntimeException("Plan introuvable : " + id);
        }
        abonnementRepository.deleteById(id);
    }

    @Override
    public Abonnement getAbonnementById(Long id) {
        return abonnementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan introuvable : " + id));
    }

    @Override
    public List<Abonnement> getAllAbonnements() {
        return abonnementRepository.findAll();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // User subscription lifecycle
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Subscribes {@code userId} to plan {@code abonnementId}.
     *
     * <ul>
     *   <li>Acquires a pessimistic write lock on the user's existing subscriptions
     *       to prevent concurrent overlapping inserts.</li>
     *   <li>If no non-expired/non-exhausted subscription exists, the new one starts
     *       today → ACTIVATED_NOW.</li>
     *   <li>Otherwise it starts the day after the latest queued end date → QUEUED_NEXT.</li>
     * </ul>
     */
    @Override
    @Transactional
    public SubscribeResponseDTO subscribe(Long userId, Long abonnementId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + userId));

        Abonnement plan = abonnementRepository.findById(abonnementId)
                .orElseThrow(() -> new RuntimeException("Plan introuvable : " + abonnementId));

        // Pessimistic lock – prevents two concurrent requests from creating overlapping periods
        List<UserAbonnement> existing = userAbonnementRepository.findByUserForUpdate(user);

        // Refresh statuses in memory before computing the new start date
        existing.forEach(this::applyComputedStatus);

        LocalDate today = LocalDate.now();

        // Find the latest end date among subscriptions that have not yet expired/exhausted
        Optional<LocalDate> latestEndDate = existing.stream()
                .filter(ua -> ua.getStatus() == UserAbonnementStatus.ACTIVE
                           || ua.getStatus() == UserAbonnementStatus.QUEUED)
                .map(UserAbonnement::getDateFin)
                .max(Comparator.naturalOrder());

        LocalDate newDateDebut = latestEndDate.map(d -> d.plusDays(1)).orElse(today);
        LocalDate newDateFin   = newDateDebut.plusMonths(plan.getDureeEnMois()).minusDays(1);

        UserAbonnementStatus status = newDateDebut.isAfter(today)
                ? UserAbonnementStatus.QUEUED
                : UserAbonnementStatus.ACTIVE;

        SubscribeResultType resultType = (status == UserAbonnementStatus.ACTIVE)
                ? SubscribeResultType.ACTIVATED_NOW
                : SubscribeResultType.QUEUED_NEXT;

        UserAbonnement ua = UserAbonnement.builder()
                .user(user)
                .abonnement(plan)
                .dateDebut(newDateDebut)
                .dateFin(newDateFin)
                .status(status)
                .ticketsRestants(plan.getTicketsMax())
                .build();

        ua = userAbonnementRepository.save(ua);

        // Generate QR code immediately for subscriptions that start now
        String qrToken = null;
        if (status == UserAbonnementStatus.ACTIVE) {
            qrToken = generateQRCodeFor(ua);
        }

        String message = (resultType == SubscribeResultType.ACTIVATED_NOW)
                ? "✅ Abonnement activé immédiatement."
                : "📅 Abonnement en file d'attente, démarrage prévu le " + newDateDebut;

        return SubscribeResponseDTO.builder()
                .userId(userId)
                .subscriptionId(ua.getId())
                .abonnementId(plan.getId())
                .abonnementType(plan.getType())
                .dateDebut(newDateDebut)
                .dateFin(newDateFin)
                .ticketsRestants(ua.getTicketsRestants())
                .status(status)
                .resultType(resultType)
                .message(message)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAbonnementDTO> getUserAbonnements(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + userId));

        return userAbonnementRepository.findByUserOrderByDateDebutAsc(user)
                .stream()
                .map(ua -> toDTO(ua, computeStatus(ua)))
                .toList();
    }

    @Override
    @Transactional
    public SubscriptionTimelineDTO getTimeline(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + userId));

        List<UserAbonnement> all = userAbonnementRepository.findByUserOrderByDateDebutAsc(user);

        // Persist updated statuses
        all.forEach(ua -> {
            UserAbonnementStatus computed = computeStatus(ua);
            if (ua.getStatus() != computed) {
                ua.setStatus(computed);
                userAbonnementRepository.save(ua);
            }
        });

        UserAbonnementDTO current = all.stream()
                .filter(ua -> ua.getStatus() == UserAbonnementStatus.ACTIVE)
                .findFirst()
                .map(ua -> toDTO(ua, ua.getStatus()))
                .orElse(null);

        List<UserAbonnementDTO> queued = all.stream()
                .filter(ua -> ua.getStatus() == UserAbonnementStatus.QUEUED)
                .sorted(Comparator.comparing(UserAbonnement::getDateDebut))
                .map(ua -> toDTO(ua, ua.getStatus()))
                .toList();

        UserAbonnementDTO next = queued.isEmpty() ? null : queued.get(0);

        List<UserAbonnementDTO> history = all.stream()
                .filter(ua -> ua.getStatus() == UserAbonnementStatus.EXPIRED
                           || ua.getStatus() == UserAbonnementStatus.EXHAUSTED)
                .sorted(Comparator.comparing(UserAbonnement::getDateDebut).reversed())
                .map(ua -> toDTO(ua, ua.getStatus()))
                .toList();

        return SubscriptionTimelineDTO.builder()
                .currentSubscription(current)
                .nextSubscription(next)
                .queuedSubscriptions(queued)
                .history(history)
                .build();
    }

    @Override
    @Transactional
    public String getQRCode(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + userId));

        UserAbonnement active = findAndRefreshActive(user);

        // Return existing QR code or generate a new one
        return Optional.ofNullable(active.getQrCode())
                .map(QRCode::getCode)
                .orElseGet(() -> generateQRCodeFor(active));
    }

    @Override
    @Transactional
    public String scanQRCode(String code) {
        QRCode qrCode = qrCodeRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("QR code invalide"));

        UserAbonnement ua = qrCode.getUserAbonnement();

        // Always recompute status before acting
        UserAbonnementStatus currentStatus = computeStatus(ua);
        ua.setStatus(currentStatus);

        switch (currentStatus) {
            case QUEUED -> throw new RuntimeException(
                    "Abonnement pas encore actif — démarre le " + ua.getDateDebut());
            case EXPIRED -> throw new RuntimeException("Abonnement expiré depuis le " + ua.getDateFin());
            case EXHAUSTED -> throw new RuntimeException(
                    "Abonnement épuisé — plus de tickets disponibles");
            case ACTIVE -> {
                // Decrement tickets if not unlimited (unlimited plans have ticketsRestants == -1)
                if (ua.getTicketsRestants() > 0) {
                    int remaining = ua.getTicketsRestants() - 1;
                    ua.setTicketsRestants(remaining);
                    if (remaining == 0) {
                        ua.setStatus(UserAbonnementStatus.EXHAUSTED);
                    }
                }
                userAbonnementRepository.save(ua);
            }
        }

        return "✅ Ticket scanné avec succès";
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Internal helpers
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Derives the correct {@link UserAbonnementStatus} from the subscription's dates and
     * remaining ticket count. This is the single source of truth for status computation.
     */
    public UserAbonnementStatus computeStatus(UserAbonnement ua) {
        LocalDate today = LocalDate.now();

        if (ua.getDateFin().isBefore(today)) {
            return UserAbonnementStatus.EXPIRED;
        }
        if (ua.getDateDebut().isAfter(today)) {
            return UserAbonnementStatus.QUEUED;
        }
        // dateDebut <= today <= dateFin
        if (!ua.getAbonnement().isUnlimited() && ua.getTicketsRestants() <= 0) {
            return UserAbonnementStatus.EXHAUSTED;
        }
        return UserAbonnementStatus.ACTIVE;
    }

    /** Applies the computed status to {@code ua} in memory (does not save). */
    private void applyComputedStatus(UserAbonnement ua) {
        ua.setStatus(computeStatus(ua));
    }

    /** Finds the active subscription for a user, refreshing its status first. */
    private UserAbonnement findAndRefreshActive(User user) {
        List<UserAbonnement> subscriptions = userAbonnementRepository
                .findByUserOrderByDateDebutAsc(user);

        return subscriptions.stream()
                .peek(this::applyComputedStatus)
                .filter(ua -> ua.getStatus() == UserAbonnementStatus.ACTIVE)
                .findFirst()
                .orElseThrow(() -> {
                    // Check if there's a queued subscription and give a helpful message
                    Optional<UserAbonnement> queued = subscriptions.stream()
                            .filter(ua -> ua.getStatus() == UserAbonnementStatus.QUEUED)
                            .min(Comparator.comparing(UserAbonnement::getDateDebut));
                    return queued
                            .<RuntimeException>map(q -> new RuntimeException(
                                    "Aucun abonnement actif — le prochain démarre le " + q.getDateDebut()))
                            .orElseGet(() -> new RuntimeException("Aucun abonnement actif"));
                });
    }

    /** Generates and persists a new QR code for the given subscription. Returns the token. */
    private String generateQRCodeFor(UserAbonnement ua) {
        String token = UUID.randomUUID().toString();
        QRCode qrCode = QRCode.builder()
                .userAbonnement(ua)
                .code(token)
                .generatedAt(LocalDateTime.now())
                .build();
        qrCodeRepository.save(qrCode);
        ua.setQrCode(qrCode);
        return token;
    }

    /** Maps a {@link UserAbonnement} to its DTO representation. */
    private UserAbonnementDTO toDTO(UserAbonnement ua, UserAbonnementStatus status) {
        String qrToken = Optional.ofNullable(ua.getQrCode())
                .map(QRCode::getCode)
                .orElse(null);

        return UserAbonnementDTO.builder()
                .id(ua.getId())
                .userId(ua.getUser().getId())
                .abonnementId(ua.getAbonnement().getId())
                .abonnementType(ua.getAbonnement().getType())
                .abonnementDescription(ua.getAbonnement().getDescription())
                .dateDebut(ua.getDateDebut())
                .dateFin(ua.getDateFin())
                .ticketsRestants(ua.getTicketsRestants())
                .status(status)
                .qrCode(qrToken)
                .build();
    }
}
