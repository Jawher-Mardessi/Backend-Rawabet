package org.example.rawabet.services;

import io.jsonwebtoken.Claims;
import org.springframework.context.MessageSource;
import java.util.Locale;
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
import org.example.rawabet.repositories.UserRepository;
import org.example.rawabet.security.JwtService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarteFideliteServiceImpl implements ICarteFideliteService {

    private final CarteFideliteRepository   carteRepository;
    private final FidelityHistoryRepository historyRepository;
    private final IAuthService              authService;
    private final UserRepository            userRepository;
    private final JwtService                jwtService;
    private final MessageSource              messageSource;

    // ── Get my carte ───────────────────────────────────────────────────────
    @Override
    public CarteFideliteResponse getMyCarte() {
        User user = authService.getAuthenticatedUser();
        CarteFidelite carte = carteRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException(messageSource.getMessage("fidelity.carte.notfound", null, Locale.ENGLISH)));
        handleExpiration(carte);
        return mapToResponse(carte);
    }

    @Override
    public CarteFideliteResponse getCarteByUser(User user) {
        CarteFidelite carte = carteRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException(messageSource.getMessage("fidelity.carte.notfound", null, Locale.ENGLISH)));
        return mapToResponse(carte);
    }

    // ── Add points (système automatique) ──────────────────────────────────
    @Override
    @Transactional
    public void addPoints(User user, int points, ActionType action) {
        if (points <= 0)  throw new RuntimeException(messageSource.getMessage("fidelity.points.positive", null, Locale.ENGLISH));
        if (points > 100) throw new RuntimeException(messageSource.getMessage("fidelity.points.suspicious", null, Locale.ENGLISH));

        checkSelfPointsGuard(user);

        CarteFidelite carte = carteRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException(messageSource.getMessage("fidelity.carte.notfound", null, Locale.ENGLISH)));
        handleExpiration(carte);

        int newPoints = carte.getPoints() + points;
        carte.setPoints(newPoints);
        carte.setLevel(calculateLevel(newPoints));
        carteRepository.save(carte);
        saveHistory(user, action, points);
    }

    // ── Add points (admin manuel) — signature interface : Long userId ──────
    @Override
    @Transactional
    public void addPointsByAdmin(Long userId, int points, ActionType action) {
        if (points <= 0)    throw new RuntimeException(messageSource.getMessage("fidelity.points.positive", null, Locale.ENGLISH));
        if (points > 1000)  throw new RuntimeException(messageSource.getMessage("fidelity.points.admin_max", null, Locale.ENGLISH));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException(messageSource.getMessage("user.notfound", null, Locale.ENGLISH)));

        checkSelfPointsGuard(user);

        CarteFidelite carte = carteRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException(messageSource.getMessage("fidelity.carte.notfound", null, Locale.ENGLISH)));
        handleExpiration(carte);

        int newPoints = carte.getPoints() + points;
        carte.setPoints(newPoints);
        carte.setLevel(calculateLevel(newPoints));
        carteRepository.save(carte);
        saveHistory(user, action, points);
    }

    // ── Get my history (paginé) ────────────────────────────────────────────
    @Override
    public Page<FidelityHistoryResponse> getMyHistory(Pageable pageable) {
        User user = authService.getAuthenticatedUser();

        // Récupérer la page depuis le repository
        Page<FidelityHistory> page = historyRepository.findByUser(user, pageable);
        return page.map(this::mapToHistoryResponse);
    }

    // ── Dashboard client ───────────────────────────────────────────────────
    @Override
    public LoyaltyDashboardResponse getDashboard() {
        User user = authService.getAuthenticatedUser();
        CarteFidelite carte = carteRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException(messageSource.getMessage("fidelity.carte.notfound", null, Locale.ENGLISH)));
        handleExpiration(carte);

        // Historique récent (10 derniers) pour le dashboard
        List<FidelityHistoryResponse> history = historyRepository.findByUser(user)
                .stream()
                .map(this::mapToHistoryResponse)
                .limit(10)
                .toList();

        return LoyaltyDashboardResponse.builder()
                .carte(mapToResponse(carte))
                .history(history)
                .rewards(getAvailableRewards())
                .build();
    }

    // ── Redeem reward ──────────────────────────────────────────────────────
    @Override
    @Transactional
    public RewardResponse redeemReward(RewardType reward) {
        User user = authService.getAuthenticatedUser();
        CarteFidelite carte = carteRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Carte not found"));
        handleExpiration(carte);

        int cost = reward.getPointsCost();
        if (carte.getPoints() < cost) {
            throw new RuntimeException(messageSource.getMessage("fidelity.points.insufficient", new Object[]{cost, carte.getPoints()}, Locale.ENGLISH));
        }

        int newPoints = carte.getPoints() - cost;
        carte.setPoints(newPoints);
        carte.setLevel(calculateLevel(newPoints));
        carteRepository.save(carte);
        saveHistory(user, ActionType.REWARD_REDEEMED, -cost);

        return RewardResponse.builder()
                .reward(reward)
                .pointsDepensés(cost)
                .pointsRestants(newPoints)
                .message(messageSource.getMessage("fidelity.reward.redeemed", new Object[]{reward.name()}, Locale.ENGLISH))
                .build();
    }

    // ── Available rewards ──────────────────────────────────────────────────
    @Override
    public List<RewardType> getAvailableRewards() {
        User user = authService.getAuthenticatedUser();
        CarteFidelite carte = carteRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException(messageSource.getMessage("fidelity.carte.notfound", null, Locale.ENGLISH)));

        return Arrays.stream(RewardType.values())
                .filter(r -> carte.getPoints() >= r.getPointsCost())
                .toList();
    }

    // ── Stats admin ────────────────────────────────────────────────────────
    @Override
    public CarteStatsResponse getStats() {
        long totalClients = carteRepository.count();
        long totalSilver  = carteRepository.countByLevel(Level.SILVER);
        long totalGold    = carteRepository.countByLevel(Level.GOLD);
        long totalVip     = carteRepository.countByLevel(Level.VIP);
        long totalPoints  = carteRepository.sumAllPoints();

        return CarteStatsResponse.builder()
                .totalClients(totalClients)
                .totalSilver(totalSilver)
                .totalGold(totalGold)
                .totalVip(totalVip)
                .totalPointsDistribues(totalPoints)
                .build();
    }

    // ── Admin overview ─────────────────────────────────────────────────────
    @Override
    public LoyaltyAdminOverviewResponse getAdminOverview() {
        return LoyaltyAdminOverviewResponse.builder()
                .stats(getStats())
                .topClients(getTopClients())
                .build();
    }

    // ── Top clients ────────────────────────────────────────────────────────
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

    @Override
    public List<TopClientResponse> getTopClients(int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 10));
        return getTopClients().stream().limit(safeLimit).toList();
    }

    // ── Search transfer recipients ─────────────────────────────────────────
    @Override
    public List<TransferRecipientResponse> searchTransferRecipients(String query) {
        User currentUser = authService.getAuthenticatedUser();
        String safeQuery = query == null ? "" : query.trim();

        if (safeQuery.length() < 2) return List.of();

        return userRepository.searchByNomOrEmail(safeQuery).stream()
                .filter(user -> !user.getId().equals(currentUser.getId()))
                .limit(10)
                .map(user -> TransferRecipientResponse.builder()
                        .id(user.getId())
                        .nom(user.getNom())
                        .email(user.getEmail())
                        .build())
                .toList();
    }

    // ── Transfer points ────────────────────────────────────────────────────
    @Override
    @Transactional
    public void transferPoints(Long toUserId, int points) {
        User fromUser = authService.getAuthenticatedUser();

        if (points <= 0)
            throw new RuntimeException(messageSource.getMessage("fidelity.points.positive", null, Locale.ENGLISH));
        if (fromUser.getId().equals(toUserId))
            throw new RuntimeException(messageSource.getMessage("fidelity.transfer.self", null, Locale.ENGLISH));

        User toUser = userRepository.findById(toUserId)
            .orElseThrow(() -> new RuntimeException(messageSource.getMessage("user.recipient.notfound", null, Locale.ENGLISH)));

        CarteFidelite fromCarte = carteRepository.findByUser(fromUser)
            .orElseThrow(() -> new RuntimeException(messageSource.getMessage("fidelity.carte.sender_notfound", null, Locale.ENGLISH)));

        if (fromCarte.getPoints() < points)
            throw new RuntimeException(messageSource.getMessage("fidelity.points.insufficient_sender", new Object[]{fromCarte.getPoints()}, Locale.ENGLISH));

        CarteFidelite toCarte = carteRepository.findByUser(toUser)
            .orElseThrow(() -> new RuntimeException(messageSource.getMessage("fidelity.carte.recipient_notfound", null, Locale.ENGLISH)));

        int fromNew = fromCarte.getPoints() - points;
        fromCarte.setPoints(fromNew);
        fromCarte.setLevel(calculateLevel(fromNew));
        carteRepository.save(fromCarte);

        int toNew = toCarte.getPoints() + points;
        toCarte.setPoints(toNew);
        toCarte.setLevel(calculateLevel(toNew));
        carteRepository.save(toCarte);

        saveHistory(fromUser, ActionType.TRANSFER_OUT, -points);
        saveHistory(toUser,   ActionType.TRANSFER_IN,   points);
    }

    // ── Garde anti self-points (impersonation) ─────────────────────────────
    private void checkSelfPointsGuard(User targetUser) {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            String authHeader = attrs.getRequest().getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) return;

            Claims claims = jwtService.extractClaims(authHeader.substring(7));
            Boolean isImpersonation = claims.get("impersonation", Boolean.class);
            if (isImpersonation == null || !isImpersonation) return;

            Number impersonatedBy = (Number) claims.get("impersonatedBy");
            if (impersonatedBy == null) return;

            if (impersonatedBy.longValue() == targetUser.getId()) {
                throw new RuntimeException(
                        "Interdit — vous ne pouvez pas ajouter des points à votre propre " +
                                "compte en mode client.");
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception ignored) {
        }
    }

    // ── Private helpers ────────────────────────────────────────────────────
    private Level calculateLevel(int points) {
        if (points >= 500) return Level.VIP;
        if (points >= 200) return Level.GOLD;
        return Level.SILVER;
    }

    private void handleExpiration(CarteFidelite carte) {
        if (carte.getDateExpiration() != null
                && carte.getDateExpiration().isBefore(LocalDate.now())) {
            carte.setPoints(0);
            carte.setLevel(Level.SILVER);
            carte.setDateExpiration(LocalDate.now().plusYears(1));
            carteRepository.save(carte);
        }
    }

    private void saveHistory(User user, ActionType action, int points) {
        FidelityHistory history = new FidelityHistory();
        history.setUser(user);
        history.setAction(action);
        history.setPoints(points);
        history.setCreatedAt(Instant.now());
        historyRepository.save(history);
    }

    private CarteFideliteResponse mapToResponse(CarteFidelite carte) {
        return CarteFideliteResponse.builder()
                .points(carte.getPoints())
                .level(carte.getLevel())
                .dateExpiration(carte.getDateExpiration())
                .build();
    }

    private FidelityHistoryResponse mapToHistoryResponse(FidelityHistory h) {
        return FidelityHistoryResponse.builder()
                .action(h.getAction())
                .points(h.getPoints())
                .createdAt(h.getCreatedAt())
                .build();
    }
}