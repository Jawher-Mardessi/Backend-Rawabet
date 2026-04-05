package org.example.rawabet.services;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.CarteFideliteResponse;
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
    // 🔥 ADD POINTS (INTERNAL)
    // =========================
    @Override
    @Transactional
    public void addPoints(User user, int points, ActionType action) {

        if (points <= 0) {
            throw new RuntimeException("Points must be positive");
        }

        // 🔥 anti abus (bonus pro)
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

        // 🧾 audit
        FidelityHistory history = new FidelityHistory();
        history.setUser(user);
        history.setAction(action);
        history.setPoints(points);
        history.setCreatedAt(Instant.now());

        historyRepository.save(history);
    }

    // =========================
    // 🔥 EXPIRATION
    // =========================
    private void handleExpiration(CarteFidelite carte) {
        if (carte.isExpired()) {
            carte.setPoints(0);
            carte.setDateExpiration(LocalDate.now().plusYears(1));
            carte.setLevel(Level.SILVER);
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
    // 🔥 MAPPING DTO
    // =========================
    private CarteFideliteResponse mapToResponse(CarteFidelite carte) {
        return CarteFideliteResponse.builder()
                .points(carte.getPoints())
                .dateExpiration(carte.getDateExpiration())
                .level(carte.getLevel())
                .build();
    }
}