package org.example.rawabet.controllers;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.*;
import org.example.rawabet.enums.ActionType;
import org.example.rawabet.enums.RewardType;
import org.example.rawabet.entities.User;
import org.example.rawabet.repositories.UserRepository;
import org.example.rawabet.services.ICarteFideliteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carte")
@RequiredArgsConstructor
public class CarteFideliteController {

    private final ICarteFideliteService carteService;
    private final UserRepository        userRepository;

    // ── CLIENT ─────────────────────────────────────────────────────────────
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('FIDELITY_READ')")
    public CarteFideliteResponse getMyCarte() {
        return carteService.getMyCarte();
    }

    @GetMapping("/history")
    @PreAuthorize("hasAuthority('FIDELITY_READ')")
    public Page<FidelityHistoryResponse> getMyHistory(
            @RequestParam(defaultValue = "0")          int page,
            @RequestParam(defaultValue = "20")         int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc")       String direction) {

        Sort sortObj = direction.equalsIgnoreCase("asc")
                ? Sort.by(sort).ascending()
                : Sort.by(sort).descending();

        return carteService.getMyHistory(PageRequest.of(page, size, sortObj));
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('FIDELITY_READ')")
    public LoyaltyDashboardResponse getDashboard() {
        return carteService.getDashboard();
    }

    @GetMapping("/rewards")
    @PreAuthorize("hasAuthority('FIDELITY_READ')")
    public List<RewardType> getAvailableRewards() {
        return carteService.getAvailableRewards();
    }

    @PostMapping("/redeem")
    @PreAuthorize("hasAuthority('FIDELITY_READ')")
    public RewardResponse redeemReward(@RequestParam RewardType reward) {
        return carteService.redeemReward(reward);
    }

    @GetMapping("/transfer/recipients")
    @PreAuthorize("hasAuthority('FIDELITY_READ')")
    public List<TransferRecipientResponse> searchTransferRecipients(@RequestParam String query) {
        return carteService.searchTransferRecipients(query);
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasAuthority('FIDELITY_READ')")
    public String transferPoints(@RequestParam Long toUserId, @RequestParam int points) {
        carteService.transferPoints(toUserId, points);
        return "✅ " + points + " points transférés avec succès !";
    }

    // ── ADMIN ──────────────────────────────────────────────────────────────
    @PostMapping("/admin/add-points/{userId}")
    @PreAuthorize("hasAuthority('FIDELITY_UPDATE')")
    public CarteFideliteResponse addPointsByAdmin(
            @PathVariable Long userId,
            @RequestParam int points) {
        // addPointsByAdmin(Long userId, ...) résout lui-même le User en interne
        carteService.addPointsByAdmin(userId, points, ActionType.BONUS);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return carteService.getCarteByUser(user);
    }

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

    @GetMapping("/admin/top")
    @PreAuthorize("hasAuthority('FIDELITY_UPDATE')")
    public List<TopClientResponse> getTopClients(
            @RequestParam(required = false) Integer limit) {
        return limit == null ? carteService.getTopClients() : carteService.getTopClients(limit);
    }
}