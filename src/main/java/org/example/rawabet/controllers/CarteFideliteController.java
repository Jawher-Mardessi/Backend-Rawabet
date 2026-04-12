package org.example.rawabet.controllers;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.*;
import org.example.rawabet.entities.User;
import org.example.rawabet.enums.ActionType;
import org.example.rawabet.enums.RewardType;
import org.example.rawabet.repositories.UserRepository;
import org.example.rawabet.services.ICarteFideliteService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carte")
@RequiredArgsConstructor
public class CarteFideliteController {

    private final ICarteFideliteService carteService;
    private final UserRepository userRepository;

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('FIDELITY_READ')")
    public CarteFideliteResponse getMyCarte() {
        return carteService.getMyCarte();
    }

    @PostMapping("/admin/add-points/{userId}")
    @PreAuthorize("hasAuthority('FIDELITY_UPDATE')")
    public CarteFideliteResponse addPointsByAdmin(
            @PathVariable Long userId,
            @RequestParam int points) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        carteService.addPointsByAdmin(user, points, ActionType.BONUS);

        return carteService.getCarteByUser(user);
    }

    @GetMapping("/history")
    @PreAuthorize("hasAuthority('FIDELITY_READ')")
    public List<FidelityHistoryResponse> getMyHistory() { // ✅ DTO
        return carteService.getMyHistory();
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('FIDELITY_READ')")
    public LoyaltyDashboardResponse getDashboard() {
        return carteService.getDashboard();
    }

    // 🎁 VOIR REWARDS DISPONIBLES
    @GetMapping("/rewards")
    @PreAuthorize("hasAuthority('FIDELITY_READ')")
    public List<RewardType> getAvailableRewards() {
        return carteService.getAvailableRewards();
    }

    // 🎁 UTILISER UN REWARD
    @PostMapping("/redeem")
    @PreAuthorize("hasAuthority('FIDELITY_READ')")
    public RewardResponse redeemReward(@RequestParam RewardType reward) {
        return carteService.redeemReward(reward);
    }

    // 📊 STATS (SUPER_ADMIN)
    @GetMapping("/admin/stats")
    @PreAuthorize("hasAuthority('FIDELITY_UPDATE')")
    public CarteStatsResponse getStats() {
        return carteService.getStats();
    }

    @GetMapping("/admin/overview")
    @PreAuthorize("hasAuthority('FIDELITY_UPDATE')")
    public LoyaltyAdminOverviewResponse getAdminOverview() {
        return carteService.getAdminOverview();
    }

    // 🏆 TOP 10 (SUPER_ADMIN)
    @GetMapping("/admin/top")
    @PreAuthorize("hasAuthority('FIDELITY_UPDATE')")
    public List<TopClientResponse> getTopClients(@RequestParam(required = false) Integer limit) {
        if (limit == null) {
            return carteService.getTopClients();
        }
        return carteService.getTopClients(limit);
    }

    // 💸 TRANSFERT DE POINTS
    @PostMapping("/transfer")
    @PreAuthorize("hasAuthority('FIDELITY_READ')")
    public String transferPoints(
            @RequestParam Long toUserId,
            @RequestParam int points) {

        carteService.transferPoints(toUserId, points);
        return "✅ " + points + " points transférés avec succès !";
    }
}