package org.example.rawabet.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.rawabet.dto.BanRequest;
import org.example.rawabet.dto.ml.*;
import org.example.rawabet.entities.User;
import org.example.rawabet.repositories.UserRepository;
import org.example.rawabet.services.IUserService;
import org.example.rawabet.services.MlPredictionService;
import org.example.rawabet.services.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/ml")
@RequiredArgsConstructor
public class MlController {

    private final MlPredictionService mlService;
    private final UserRepository      userRepository;
    private final IUserService        userService;
    private final EmailService        emailService;

    @Value("${app.super-admin.email:admin@test.com}")
    private String superAdminEmail;

    // ── Prédictions individuelles ─────────────────────────────────────────
    @GetMapping("/predict/churn/{userId}")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    public ResponseEntity<ChurnPredictionResponse> predictChurn(@PathVariable Long userId) {
        ChurnPredictionResponse result = mlService.predictChurn(userId);
        if (result == null) return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/predict/anomaly/{userId}")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    public ResponseEntity<AnomalyPredictionResponse> predictAnomaly(@PathVariable Long userId) {
        AnomalyPredictionResponse result = mlService.predictAnomaly(userId);
        if (result == null) return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/predict/next-level/{userId}")
    @PreAuthorize("hasAuthority('FIDELITY_READ')")
    public ResponseEntity<NextLevelPredictionResponse> predictNextLevel(@PathVariable Long userId) {
        NextLevelPredictionResponse result = mlService.predictNextLevel(userId);
        if (result == null) return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/predict/reward/{userId}")
    @PreAuthorize("hasAuthority('FIDELITY_READ')")
    public ResponseEntity<RewardPredictionResponse> predictReward(@PathVariable Long userId) {
        RewardPredictionResponse result = mlService.predictReward(userId);
        if (result == null) return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/predict/all/{userId}")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    public ResponseEntity<AllPredictionsResponse> predictAll(@PathVariable Long userId) {
        AllPredictionsResponse result = mlService.predictAll(userId);
        if (result == null) return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        return ResponseEntity.ok(result);
    }

    // ════════════════════════════════════════════════════════════════════════
    // NOUVEAU — Scan global de tous les clients
    // Retourne une vue d'ensemble ML pour le dashboard avancé
    // ════════════════════════════════════════════════════════════════════════
    @GetMapping("/scan/all")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    public ResponseEntity<List<Map<String, Object>>> scanAllClients() {
        List<User> clients = userRepository.findAll().stream()
                .filter(u -> u.isActive() && u.getRoles().stream()
                        .anyMatch(r -> r.getName().equals("CLIENT")))
                .limit(50) // max 50 pour éviter la surcharge
                .toList();

        List<Map<String, Object>> results = new ArrayList<>();

        for (User user : clients) {
            try {
                AllPredictionsResponse pred = mlService.predictAll(user.getId());
                if (pred == null) continue;

                Map<String, Object> row = new LinkedHashMap<>();
                row.put("userId",       user.getId());
                row.put("nom",          user.getNom());
                row.put("email",        user.getEmail());

                // Churn
                if (pred.getChurn() != null) {
                    row.put("churnProba",    pred.getChurn().getProbability());
                    row.put("churnLabel",    pred.getChurn().getChurnLabel());
                    row.put("churnRisk",     pred.getChurn().getRiskLevel());
                }

                // Anomalie
                if (pred.getAnomaly() != null) {
                    row.put("anomalyProba",  pred.getAnomaly().getProbability());
                    row.put("anomalyLabel",  pred.getAnomaly().getAnomalyLabel());
                    row.put("anomalyAction", pred.getAnomaly().getRecommendedAction());
                }

                // Next level
                if (pred.getNextLevel() != null) {
                    row.put("currentLevel",  pred.getNextLevel().getCurrentLevel());
                    row.put("nextLevel",     pred.getNextLevel().getPredictedLevel());
                    row.put("willUpgrade",   pred.getNextLevel().isWillUpgrade());
                }

                // Reward
                if (pred.getReward() != null) {
                    row.put("bestReward",    pred.getReward().getBestReward());
                    row.put("canRedeem",     pred.getReward().isCanRedeem());
                }

                results.add(row);
            } catch (Exception e) {
                log.warn("[ML] Scan failed for userId={}: {}", user.getId(), e.getMessage());
            }
        }

        return ResponseEntity.ok(results);
    }

    // ════════════════════════════════════════════════════════════════════════
    // NOUVEAU — Action automatique sur un client selon le score ML
    // POST /ml/auto-action/{userId}
    // Logique :
    //   - anomalie > 0.8 → ban temporaire 24h + email SUPER_ADMIN
    //   - churn HIGH     → email SUPER_ADMIN avec alerte
    // ════════════════════════════════════════════════════════════════════════
    @PostMapping("/auto-action/{userId}")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    public ResponseEntity<Map<String, Object>> autoAction(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();

        AllPredictionsResponse pred = mlService.predictAll(userId);
        if (pred == null) return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();

        Map<String, Object> result = new LinkedHashMap<>();
        List<String> actionsPerformed = new ArrayList<>();

        // ── Anomalie critique → ban 24h ──────────────────────────────────
        if (pred.getAnomaly() != null && pred.getAnomaly().getProbability() > 0.80) {
            try {
                BanRequest banReq = new BanRequest();
                banReq.setBanUntil(LocalDateTime.now().plusHours(24));
                banReq.setReason("Ban automatique ML — Score anomalie : "
                        + String.format("%.1f%%", pred.getAnomaly().getProbability() * 100));
                userService.banUser(userId, banReq);
                actionsPerformed.add("BAN_24H");
                log.warn("[ML AUTO-ACTION] userId={} banni 24h — anomalie score={}",
                        userId, pred.getAnomaly().getProbability());
            } catch (Exception e) {
                log.error("[ML AUTO-ACTION] Erreur ban userId={}: {}", userId, e.getMessage());
            }
        }

        // ── Churn HIGH → email alerte SUPER_ADMIN ────────────────────────
        if (pred.getChurn() != null && "HIGH".equals(pred.getChurn().getRiskLevel())) {
            try {
                String subject = "⚠️ Rawabet ML — Client à risque de départ";
                String body = String.format(
                        "Bonjour,\n\n" +
                                "Le modèle ML a détecté un client à HAUT risque de départ.\n\n" +
                                "Client  : %s (%s)\n" +
                                "Score   : %.1f%%\n" +
                                "Niveau  : %s\n\n" +
                                "Action recommandée : Proposer une offre personnalisée ou une récompense.\n\n" +
                                "Rawabet — Système ML automatique",
                        user.getNom(), user.getEmail(),
                        pred.getChurn().getProbability() * 100,
                        pred.getChurn().getRiskLevel()
                );
                emailService.sendSimpleAlert(superAdminEmail, subject, body);
                actionsPerformed.add("EMAIL_CHURN_ALERT");
            } catch (Exception e) {
                log.error("[ML AUTO-ACTION] Erreur email userId={}: {}", userId, e.getMessage());
            }
        }

        if (actionsPerformed.isEmpty()) {
            actionsPerformed.add("AUCUNE_ACTION");
        }

        result.put("userId",   userId);
        result.put("actions",  actionsPerformed);
        result.put("churnRisk", pred.getChurn() != null ? pred.getChurn().getRiskLevel() : "N/A");
        result.put("anomalyScore", pred.getAnomaly() != null ? pred.getAnomaly().getProbability() : 0);

        return ResponseEntity.ok(result);
    }

    // ── Health ─────────────────────────────────────────────────────────────
    @GetMapping("/health")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    public ResponseEntity<String> mlHealth() {
        try {
            org.springframework.web.client.RestTemplate rt =
                    new org.springframework.web.client.RestTemplate();
            String resp = rt.getForObject("http://localhost:8000/health", String.class);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("ML API indisponible : " + e.getMessage());
        }
    }
}