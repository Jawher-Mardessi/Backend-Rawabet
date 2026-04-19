package org.example.rawabet.services;

import org.example.rawabet.dto.abonnement.SubscribeResponseDTO;
import org.example.rawabet.dto.abonnement.SubscriptionTimelineDTO;
import org.example.rawabet.dto.abonnement.UserAbonnementDTO;
import org.example.rawabet.entities.Abonnement;

import java.util.List;

public interface IAbonnementService {

    // ── Plan (template) CRUD ──────────────────────────────────────────────────

    Abonnement addAbonnement(Abonnement abonnement);

    Abonnement updateAbonnement(Abonnement abonnement);

    void deleteAbonnement(Long id);

    Abonnement getAbonnementById(Long id);

    List<Abonnement> getAllAbonnements();

    // ── User subscription lifecycle ───────────────────────────────────────────

    /**
     * Subscribes a user to a plan.
     * The new subscription starts today if there is no active/queued subscription,
     * or the day after the last queued end date otherwise.
     */
    SubscribeResponseDTO subscribe(Long userId, Long abonnementId);

    /**
     * Returns all subscriptions for a user sorted by dateDebut ascending,
     * with up-to-date statuses.
     */
    List<UserAbonnementDTO> getUserAbonnements(Long userId);

    /**
     * Returns the full subscription timeline for a user:
     * current active, next queued, all queued, and expired history.
     */
    SubscriptionTimelineDTO getTimeline(Long userId);

    /**
     * Returns (or generates) the QR-code token for the user's currently active subscription.
     * Throws a {@link RuntimeException} if there is no active subscription.
     */
    String getQRCode(Long userId);

    /**
     * Validates a QR-code scan: decrements the ticket count on the active subscription
     * and returns a success message.
     * Throws a descriptive {@link RuntimeException} for invalid, queued, or expired QR codes.
     */
    String scanQRCode(String code);
}
