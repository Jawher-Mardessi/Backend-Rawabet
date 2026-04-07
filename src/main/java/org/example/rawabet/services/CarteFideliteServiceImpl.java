package org.example.rawabet.services;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.*;
import org.example.rawabet.entities.CarteFidelite;
import org.example.rawabet.entities.FidelityHistory;
import org.example.rawabet.entities.User;
import org.example.rawabet.enums.ActionType;
import org.example.rawabet.enums.Level;
import org.example.rawabet.enums.RewardType;
import org.example.rawabet.repositories.CarteFideliteRepository;
import org.example.rawabet.repositories.FidelityHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.rawabet.repositories.UserRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarteFideliteServiceImpl implements ICarteFideliteService {

    private final CarteFideliteRepository carteRepository;
    private final FidelityHistoryRepository historyRepository;
    private final IAuthService authService;
    private final UserRepository userRepository;

    // =========================
    // 🔐 GET MY CARTE
    // =========================
    @Override
    public CarteFideliteResponse getMyCarte() {
        User user = authService.getAuthenticatedUser();
        CarteFidelite carte = carteRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Carte not found"));
        handleExpiration(carte);
        return mapToResponse(carte);
    }

    // =========================
    // 🔐 GET CARTE BY USER (ADMIN)
    // =========================
    @Override
    public CarteFideliteResponse getCarteByUser(User user) {
        CarteFidelite carte = carteRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Carte not found"));
        return mapToResponse(carte);
    }

    // =========================
    // 🔥 ADD POINTS (SYSTÈME)
    // =========================
    @Override
    @Transactional
    public void addPoints(User user, int points, ActionType action) {

        if (points <= 0) {
            throw new RuntimeException("Points must be positive");
        }
        if (points > 100) {
            throw new RuntimeException("Suspicious points value");
        }

        CarteFidelite carte = carteRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Carte not found"));

        handleExpiration(carte);

        int newPoints = carte.getPoints() + points;
        carte.setPoints(newPoints);
        carte.setLevel(calculateLevel(newPoints));
        carteRepository.save(carte);

        saveHistory(user, action, points);
    }

    // =========================
    // 👑 ADD POINTS (ADMIN)
    // =========================
    @Override
    @Transactional
    public void addPointsByAdmin(User user, int points, ActionType action) {

        if (points <= 0) {
            throw new RuntimeException("Points must be positive");
        }
        if (points > 1000) {
            throw new RuntimeException("Max 1000 points par opération admin");
        }

        CarteFidelite carte = carteRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Carte not found"));

        handleExpiration(carte);

        int newPoints = carte.getPoints() + points;
        carte.setPoints(newPoints);
        carte.setLevel(calculateLevel(newPoints));
        carteRepository.save(carte);

        saveHistory(user, action, points);
    }

    // =========================
    // 📋 GET MY HISTORY
    // =========================
    @Override
    public List<FidelityHistoryResponse> getMyHistory() {
        User user = authService.getAuthenticatedUser();
        return historyRepository.findByUser(user)
                .stream()
                .map(this::mapToHistoryResponse)
                .toList();
    }

    // =========================
    // 🔥 EXPIRATION
    // =========================
    private void handleExpiration(CarteFidelite carte) {
        if (carte.isExpired()) {
            carte.setPoints(0);
            carte.setDateExpiration(LocalDate.now().plusYears(1));
            carte.setLevel(Level.SILVER);
            carteRepository.save(carte);
        }
    }

    // =========================
    // 🎯 LEVEL
    // =========================
    private Level calculateLevel(int points) {
        if (points >= 500) return Level.VIP;
        if (points >= 200) return Level.GOLD;
        return Level.SILVER;
    }

    // =========================
    // 🧾 SAVE HISTORY
    // =========================
    private void saveHistory(User user, ActionType action, int points) {
        FidelityHistory history = new FidelityHistory();
        history.setUser(user);
        history.setAction(action);
        history.setPoints(points);
        history.setCreatedAt(Instant.now());
        historyRepository.save(history);
    }

    // =========================
    // 🔥 MAPPING CARTE DTO
    // =========================
    private CarteFideliteResponse mapToResponse(CarteFidelite carte) {
        return CarteFideliteResponse.builder()
                .points(carte.getPoints())
                .dateExpiration(carte.getDateExpiration())
                .level(carte.getLevel())
                .build();
    }

    // =========================
    // 🔥 MAPPING HISTORY DTO
    // =========================
    private FidelityHistoryResponse mapToHistoryResponse(FidelityHistory h) {
        return FidelityHistoryResponse.builder()
                .action(h.getAction())
                .points(h.getPoints())
                .createdAt(h.getCreatedAt())
                .build();
    }


    // =========================
// 🎁 REDEEM REWARD
// =========================
    @Override
    @Transactional
    public RewardResponse redeemReward(RewardType reward) {

        User user = authService.getAuthenticatedUser();

        CarteFidelite carte = carteRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Carte not found"));

        handleExpiration(carte);

        int cost = reward.getPointsCost();

        // ✅ vérifier points suffisants
        if (carte.getPoints() < cost) {
            throw new RuntimeException(
                    "Points insuffisants — il vous faut " + cost + " pts, vous avez " + carte.getPoints() + " pts"
            );
        }

        // ✅ déduire les points
        int newPoints = carte.getPoints() - cost;
        carte.setPoints(newPoints);
        carte.setLevel(calculateLevel(newPoints));
        carteRepository.save(carte);

        // 🧾 audit
        saveHistory(user, ActionType.BONUS, -cost);

        return RewardResponse.builder()
                .reward(reward)
                .pointsDepensés(cost)
                .pointsRestants(newPoints)
                .message("✅ " + reward.getDescription() + " activé avec succès !")
                .build();
    }

    // =========================
// 🎁 GET AVAILABLE REWARDS
// =========================
    @Override
    public List<RewardType> getAvailableRewards() {
        User user = authService.getAuthenticatedUser();
        CarteFidelite carte = carteRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Carte not found"));

        return Arrays.stream(RewardType.values())
                .filter(r -> carte.getPoints() >= r.getPointsCost())
                .toList();
    }

    // =========================
// 📊 GET STATS (ADMIN)
// =========================
    @Override
    public CarteStatsResponse getStats() {
        long totalClients = carteRepository.count();
        long totalSilver = carteRepository.countByLevel(Level.SILVER);
        long totalGold = carteRepository.countByLevel(Level.GOLD);
        long totalVip = carteRepository.countByLevel(Level.VIP);
        long totalPoints = carteRepository.sumAllPoints();

        return CarteStatsResponse.builder()
                .totalClients(totalClients)
                .totalSilver(totalSilver)
                .totalGold(totalGold)
                .totalVip(totalVip)
                .totalPointsDistribués(totalPoints)
                .build();
    }

    // =========================
// 🏆 GET TOP CLIENTS (ADMIN)
// =========================
    @Override
    public List<TopClientResponse> getTopClients() {
        return carteRepository.findTop10ByOrderByPointsDesc()
                .stream()
                .map(c -> TopClientResponse.builder()
                        .nom(c.getUser().getNom())
                        .email(c.getUser().getEmail())
                        .points(c.getPoints())
                        .level(c.getLevel())
                        .build())
                .toList();
    }

    // =========================
// 💸 TRANSFER POINTS
// =========================
    @Override
    @Transactional
    public void transferPoints(Long toUserId, int points) {

        // ✅ récupérer user connecté (expéditeur)
        User fromUser = authService.getAuthenticatedUser();

        // ✅ vérifier points positifs
        if (points <= 0) {
            throw new RuntimeException("Points must be positive");
        }

        // ✅ pas de transfert à soi-même
        if (fromUser.getId().equals(toUserId)) {
            throw new RuntimeException("Impossible de transférer à vous-même");
        }

        // ✅ récupérer destinataire
        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new RuntimeException("Destinataire non trouvé"));

        // ✅ récupérer carte expéditeur
        CarteFidelite fromCarte = carteRepository.findByUser(fromUser)
                .orElseThrow(() -> new RuntimeException("Carte expéditeur non trouvée"));

        // ✅ vérifier points suffisants
        if (fromCarte.getPoints() < points) {
            throw new RuntimeException(
                    "Points insuffisants — vous avez " + fromCarte.getPoints() + " pts"
            );
        }

        // ✅ récupérer carte destinataire
        CarteFidelite toCarte = carteRepository.findByUser(toUser)
                .orElseThrow(() -> new RuntimeException("Carte destinataire non trouvée"));

        // ✅ déduire points expéditeur
        int fromNewPoints = fromCarte.getPoints() - points;
        fromCarte.setPoints(fromNewPoints);
        fromCarte.setLevel(calculateLevel(fromNewPoints));
        carteRepository.save(fromCarte);

        // ✅ ajouter points destinataire
        int toNewPoints = toCarte.getPoints() + points;
        toCarte.setPoints(toNewPoints);
        toCarte.setLevel(calculateLevel(toNewPoints));
        carteRepository.save(toCarte);

        // 🧾 audit expéditeur
        saveHistory(fromUser, ActionType.BONUS, -points);

        // 🧾 audit destinataire
        saveHistory(toUser, ActionType.BONUS, points);
    }
}