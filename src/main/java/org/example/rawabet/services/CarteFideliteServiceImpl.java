package org.example.rawabet.services;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.CarteFideliteResponse;
import org.example.rawabet.dto.FidelityHistoryResponse;
import org.example.rawabet.entities.CarteFidelite;
import org.example.rawabet.entities.FidelityHistory;
import org.example.rawabet.entities.User;
import org.example.rawabet.enums.ActionType;
import org.example.rawabet.enums.Level;
import org.example.rawabet.repositories.CarteFideliteRepository;
import org.example.rawabet.repositories.FidelityHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarteFideliteServiceImpl implements ICarteFideliteService {

    private final CarteFideliteRepository carteRepository;
    private final FidelityHistoryRepository historyRepository;
    private final IAuthService authService;

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
}