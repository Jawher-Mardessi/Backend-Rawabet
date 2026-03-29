package org.example.rawabet.services;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.entities.Abonnement;
import org.example.rawabet.entities.User;
import org.example.rawabet.entities.UserAbonnement;
import org.example.rawabet.enums.AbonnementType;
import org.example.rawabet.repositories.AbonnementRepository;
import org.example.rawabet.repositories.UserAbonnementRepository;
import org.example.rawabet.repositories.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AbonnementServiceImpl {

    private final AbonnementRepository abonnementRepository;
    private final UserAbonnementRepository userAbonnementRepository;
    private final UserRepository userRepository;

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
    public UserAbonnement subscribe(Long userId, Long abonnementId) {
        cleanupExpiredUserAbonnements();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Abonnement abonnement = abonnementRepository.findById(abonnementId)
                .orElseThrow(() -> new RuntimeException("Abonnement not found"));

        UserAbonnement userAbonnement = userAbonnementRepository.findByUserId(userId)
                .orElseGet(UserAbonnement::new);

        applySubscription(userAbonnement, user, abonnement);
        return userAbonnementRepository.save(userAbonnement);
    }

    public List<UserAbonnement> getUserAbonnements() {
        cleanupExpiredUserAbonnements();
        return userAbonnementRepository.findAll();
    }

    @Transactional
    @Scheduled(cron = "0 0 * * * *")
    public long cleanupExpiredUserAbonnements() {
        return userAbonnementRepository.deleteByDateFinBefore(LocalDate.now());
    }

    private void applySubscription(UserAbonnement userAbonnement, User user, Abonnement abonnement) {
        userAbonnement.setUser(user);
        userAbonnement.setAbonnement(abonnement);

        if (abonnement.isIllimite()) {
            userAbonnement.setTicketsRestants(-1);
        } else {
            userAbonnement.setTicketsRestants(abonnement.getNbTicketsParMois());
        }

        userAbonnement.setDateDebut(LocalDate.now());
        userAbonnement.setDateFin(LocalDate.now().plusMonths(1));
    }
}
