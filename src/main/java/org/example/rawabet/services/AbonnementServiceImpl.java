package org.example.rawabet.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.UserSubscriptionResponse;
import org.example.rawabet.entities.Abonnement;
import org.example.rawabet.entities.QRCode;
import org.example.rawabet.entities.User;
import org.example.rawabet.entities.UserAbonnement;
import org.example.rawabet.enums.ActionType;
import org.example.rawabet.enums.AbonnementType;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AbonnementServiceImpl {

    private static final int UNLIMITED_TICKETS = 10000;

    private final AbonnementRepository abonnementRepository;
    private final UserAbonnementRepository userAbonnementRepository;
    private final UserRepository userRepository;
    private final ICarteFideliteService carteFideliteService;
    private final QRCodeRepository qrCodeRepository;

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

    public List<Abonnement> getAllAbonnements() {
        return abonnementRepository.findAll();
    }

    @Transactional
    public UserSubscriptionResponse subscribe(Long userId, Long abonnementId) {
        cleanupExpiredUserAbonnements();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Abonnement abonnement = abonnementRepository.findById(abonnementId)
                .orElseThrow(() -> new RuntimeException("Abonnement not found"));

        UserAbonnement userAbonnement = userAbonnementRepository.findByUserId(userId)
                .orElseGet(UserAbonnement::new);

        applySubscription(userAbonnement, user, abonnement);
        UserAbonnement savedSubscription = userAbonnementRepository.save(userAbonnement);

        // Ensure the user has a loyalty card before awarding subscription points.
        carteFideliteService.getCarteByUser(user);

        int fidelityPoints = getSubscriptionFidelityPoints(abonnement.getType());
        if (fidelityPoints > 0) {
            carteFideliteService.addPoints(user, fidelityPoints, ActionType.BONUS);
        }

        generateQRCode(savedSubscription);

        return mapToSubscriptionResponse(savedSubscription);
    }

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
        UserAbonnement userAbonnement = userAbonnementRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        QRCode qrCode = qrCodeRepository.findByUserAbonnementId(userAbonnement.getId())
                .orElseThrow(() -> new RuntimeException("QR Code not found for this user"));

        return qrCode.getCode();
    }

    public List<UserAbonnement> getUserAbonnements() {
        cleanupExpiredUserAbonnements();
        return userAbonnementRepository.findAll();
    }

    public UserSubscriptionResponse getSubscriptionByUserId(Long userId) {
        cleanupExpiredUserAbonnements();
        UserAbonnement userAbonnement = userAbonnementRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Subscription not found for this user"));

        return mapToSubscriptionResponse(userAbonnement);
    }

    @Transactional
    @Scheduled(cron = "0 0 * * * *")
    public long cleanupExpiredUserAbonnements() {
        return userAbonnementRepository.deleteByDateFinBefore(LocalDate.now());
    }

    private void applySubscription(UserAbonnement userAbonnement, User user, Abonnement abonnement) {
        userAbonnement.setUser(user);
        userAbonnement.setAbonnement(abonnement);

        userAbonnement.setTicketsRestants(resolveTicketsRestants(abonnement));

        userAbonnement.setDateDebut(LocalDate.now());
        userAbonnement.setDateFin(LocalDate.now().plusMonths(1));
    }

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
}
