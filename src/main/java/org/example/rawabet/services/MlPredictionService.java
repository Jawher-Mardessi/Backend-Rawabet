package org.example.rawabet.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.rawabet.dto.ml.*;
import org.example.rawabet.entities.*;
import org.example.rawabet.enums.SubscriptionStatus;
import org.example.rawabet.repositories.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Service ML — communique avec l'API FastAPI Python.
 *
 * Construit les features depuis la BDD Rawabet et appelle
 * les endpoints /predict/churn, /predict/anomaly, etc.
 *
 * Si l'API ML est indisponible, retourne null sans planter le backend.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MlPredictionService {

    @Value("${ml.api.url:http://localhost:8000}")
    private String mlApiUrl;

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final CarteFideliteRepository carteRepository;
    private final FidelityHistoryRepository historyRepository;
    private final ReservationCinemaRepository reservationCinemaRepository;

    // ── CHURN ─────────────────────────────────────────────────────────────
    public ChurnPredictionResponse predictChurn(Long userId) {
        try {
            ClientFeaturesRequest features = buildFeatures(userId);
            if (features == null) return null;
            return restTemplate.postForObject(
                    mlApiUrl + "/predict/churn", features,
                    ChurnPredictionResponse.class);
        } catch (RestClientException e) {
            log.warn("[ML] API indisponible pour churn userId={}: {}", userId, e.getMessage());
            return null;
        }
    }

    // ── ANOMALY ───────────────────────────────────────────────────────────
    public AnomalyPredictionResponse predictAnomaly(Long userId) {
        try {
            ClientFeaturesRequest features = buildFeatures(userId);
            if (features == null) return null;
            return restTemplate.postForObject(
                    mlApiUrl + "/predict/anomaly", features,
                    AnomalyPredictionResponse.class);
        } catch (RestClientException e) {
            log.warn("[ML] API indisponible pour anomaly userId={}: {}", userId, e.getMessage());
            return null;
        }
    }

    // ── NEXT LEVEL ────────────────────────────────────────────────────────
    public NextLevelPredictionResponse predictNextLevel(Long userId) {
        try {
            ClientFeaturesRequest features = buildFeatures(userId);
            if (features == null) return null;
            return restTemplate.postForObject(
                    mlApiUrl + "/predict/next-level", features,
                    NextLevelPredictionResponse.class);
        } catch (RestClientException e) {
            log.warn("[ML] API indisponible pour next-level userId={}: {}", userId, e.getMessage());
            return null;
        }
    }

    // ── REWARD ────────────────────────────────────────────────────────────
    public RewardPredictionResponse predictReward(Long userId) {
        try {
            ClientFeaturesRequest features = buildFeatures(userId);
            if (features == null) return null;
            return restTemplate.postForObject(
                    mlApiUrl + "/predict/reward", features,
                    RewardPredictionResponse.class);
        } catch (RestClientException e) {
            log.warn("[ML] API indisponible pour reward userId={}: {}", userId, e.getMessage());
            return null;
        }
    }

    // ── ALL ───────────────────────────────────────────────────────────────
    public AllPredictionsResponse predictAll(Long userId) {
        try {
            ClientFeaturesRequest features = buildFeatures(userId);
            if (features == null) return null;
            return restTemplate.postForObject(
                    mlApiUrl + "/predict/all", features,
                    AllPredictionsResponse.class);
        } catch (RestClientException e) {
            log.warn("[ML] API indisponible pour all userId={}: {}", userId, e.getMessage());
            return null;
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // Construction des features depuis la BDD
    // ════════════════════════════════════════════════════════════════════════
    private ClientFeaturesRequest buildFeatures(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return null;

        CarteFidelite carte = carteRepository.findByUser(user).orElse(null);

        // ── Ancienneté ────────────────────────────────────────────────────
        int anciennete = user.getCreatedAt() != null
                ? (int) ChronoUnit.DAYS.between(
                user.getCreatedAt().toLocalDate(), LocalDate.now())
                : 30;

        // ── Abonnement ────────────────────────────────────────────────────
        Abonnement abo = user.getAbonnements() == null ? null
                : user.getAbonnements().stream()
                .filter(userAbonnement -> userAbonnement.getStatus() == SubscriptionStatus.ACTIVE)
                .map(UserAbonnement::getAbonnement)
                .findFirst()
                .orElse(null);
        int hasAbo = abo != null ? 1 : 0;
        int aboType = 0;
        if (abo != null) {
            switch (abo.getType().name()) {
                case "MENSUEL"  -> aboType = 0;
                case "ANNUEL"   -> aboType = 1;
                case "PREMIUM"  -> aboType = 2;
            }
        }

        // ── Réservations ──────────────────────────────────────────────────
        List<ReservationCinema> reservations = user.getReservationCinemas() != null
                ? user.getReservationCinemas() : List.of();

        int nbResa      = reservations.size();
        int nbAnnul     = (int) reservations.stream()
                .filter(r -> "CANCELLED".equals(r.getStatut().name())).count();
        double tauxAnnul = nbResa > 0 ? (double) nbAnnul / nbResa : 0.0;

        // Réservations des 30 derniers jours
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        int nbResa30j = (int) reservations.stream()
                .filter(r -> r.getDateReservation() != null
                        && r.getDateReservation().isAfter(thirtyDaysAgo))
                .count();

        // ── Fidélité ──────────────────────────────────────────────────────
        int points       = carte != null ? carte.getPoints() : 0;
        int levelInt     = 0;
        if (carte != null) {
            switch (carte.getLevel().name()) {
                case "GOLD" -> levelInt = 1;
                case "VIP"  -> levelInt = 2;
            }
        }

        int joursExpir = carte != null && carte.getDateExpiration() != null
                ? (int) ChronoUnit.DAYS.between(LocalDate.now(), carte.getDateExpiration())
                : 180;
        joursExpir = Math.max(0, joursExpir);

        // ── Historique actions ────────────────────────────────────────────
        List<FidelityHistory> history = historyRepository.findByUser(user);
        int totalGagnes  = history.stream().filter(h -> h.getPoints() > 0)
                .mapToInt(FidelityHistory::getPoints).sum();
        int totalDepenses = history.stream().filter(h -> h.getPoints() < 0)
                .mapToInt(h -> -h.getPoints()).sum();

        int freqCinema = (int) history.stream()
                .filter(h -> h.getAction() != null && h.getAction().name().equals("CINEMA")).count();
        int freqEvent  = (int) history.stream()
                .filter(h -> h.getAction() != null && h.getAction().name().equals("EVENT")).count();
        int freqClub   = (int) history.stream()
                .filter(h -> h.getAction() != null && h.getAction().name().equals("CLUB")).count();
        int freqBonus  = (int) history.stream()
                .filter(h -> h.getAction() != null && h.getAction().name().equals("BONUS")).count();

        // ── Feedbacks ─────────────────────────────────────────────────────
        List<Feedback> feedbacks = user.getFeedbacks() != null ? user.getFeedbacks() : List.of();
        int nbFeedbacks   = feedbacks.size();
        double noteMoyenne = feedbacks.stream()
                .mapToInt(Feedback::getNote).average().orElse(0.0);

        // ── Notifications ─────────────────────────────────────────────────
        List<Notification> notifs = user.getNotifications() != null
                ? user.getNotifications() : List.of();
        int nbNotifs = notifs.size();
        double notifLuesPct = nbNotifs > 0
                ? (double) notifs.stream().filter(Notification::isLue).count() / nbNotifs
                : 0.5;

        // ── Montant total ─────────────────────────────────────────────────
        double montantTotal = reservations.stream()
                .filter(r -> r.getPaiement() != null)
                .mapToDouble(r -> r.getPaiement().getMontant())
                .sum();

        // ── Sécurité ──────────────────────────────────────────────────────
        int loginFailed = user.getLoginFailedAttempts();
        int isLocked    = user.getLoginLockedUntil() != null ? 1 : 0;

        return ClientFeaturesRequest.builder()
                .anciennetéJours(anciennete)
                .hasAbonnement(hasAbo)
                .abonnementType(aboType)
                .nbReservations(nbResa)
                .nbAnnulations(nbAnnul)
                .tauxAnnulation(tauxAnnul)
                .nbResa30j(nbResa30j)
                .pointsActuels(points)
                .level(levelInt)
                .totalPointsGagnes(totalGagnes)
                .pointsDepenses(totalDepenses)
                .joursAvantExpiration(joursExpir)
                .freqCinema(freqCinema)
                .freqEvent(freqEvent)
                .freqClub(freqClub)
                .freqBonus(freqBonus)
                .nbFeedbacks(nbFeedbacks)
                .noteMoyenne(noteMoyenne)
                .nbNotifications(nbNotifs)
                .notifLuesPct(notifLuesPct)
                .montantTotal(montantTotal)
                .loginFailed(loginFailed)
                .isLocked(isLocked)
                .build();
    }
}
