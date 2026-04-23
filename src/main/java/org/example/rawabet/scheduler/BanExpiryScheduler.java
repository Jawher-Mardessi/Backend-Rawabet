package org.example.rawabet.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.rawabet.repositories.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Scheduler qui lève automatiquement les bans temporaires expirés.
 *
 * Sans ce scheduler, un ban temporaire n'est levé qu'au prochain login
 * de l'utilisateur concerné. Avec ce scheduler, le compte est réactivé
 * en tâche de fond toutes les 5 minutes, même si l'utilisateur ne se connecte pas.
 *
 * Prérequis : @EnableScheduling sur PicloudApplication (déjà ajouté).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BanExpiryScheduler {

    private final UserRepository userRepository;

    /**
     * S'exécute toutes les 5 minutes.
     * Cherche les users avec isActive=false ET banUntil dans le passé
     * → les réactive automatiquement.
     *
     * fixedDelay = 5 min = 300 000 ms
     * initialDelay = 30s pour laisser l'app démarrer proprement
     */
    @Scheduled(fixedDelay = 300_000, initialDelay = 30_000)
    @Transactional
    public void liftExpiredBans() {
        LocalDateTime now = LocalDateTime.now();

        // Trouver tous les users bannis temporairement dont la date est dépassée
        var expiredBans = userRepository.findByIsActiveFalseAndBanUntilBefore(now);

        if (expiredBans.isEmpty()) return;

        AtomicInteger count = new AtomicInteger(0);

        expiredBans.forEach(user -> {
            user.setActive(true);
            user.setBanUntil(null);
            user.setBanReason(null);
            // Incrémenter tokenVersion pour invalider les éventuels tokens
            // émis pendant la période de ban
            user.setTokenVersion(user.getTokenVersion() + 1);
            count.incrementAndGet();
        });

        userRepository.saveAll(expiredBans);

        log.info("✅ BanExpiryScheduler — {} ban(s) temporaire(s) levé(s) automatiquement", count.get());
    }

    /**
     * Réinitialise les verrous de connexion expirés (loginLockedUntil).
     * S'exécute toutes les 10 minutes.
     */
    @Scheduled(fixedDelay = 600_000, initialDelay = 60_000)
    @Transactional
    public void liftExpiredLoginLocks() {
        LocalDateTime now = LocalDateTime.now();

        var lockedUsers = userRepository.findByLoginLockedUntilBefore(now);

        if (lockedUsers.isEmpty()) return;

        lockedUsers.forEach(user -> {
            user.setLoginLockedUntil(null);
            user.setLoginFailedAttempts(0);
        });

        userRepository.saveAll(lockedUsers);

        log.info("✅ BanExpiryScheduler — {} verrou(s) de connexion levé(s)", lockedUsers.size());
    }
}