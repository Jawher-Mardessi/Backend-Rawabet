package org.example.rawabet.controllers;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.abonnement.SubscribeResponseDTO;
import org.example.rawabet.dto.abonnement.SubscriptionTimelineDTO;
import org.example.rawabet.dto.abonnement.UserAbonnementDTO;
import org.example.rawabet.entities.Abonnement;
import org.example.rawabet.services.IAbonnementService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/abonnements")
@RequiredArgsConstructor
public class AbonnementController {

    private final IAbonnementService abonnementService;

    // ══════════════════════════════════════════════════════════════════════════
    // Plan (template) CRUD  — admin only
    // ══════════════════════════════════════════════════════════════════════════

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    @ResponseStatus(HttpStatus.CREATED)
    public Abonnement createPlan(@RequestBody Abonnement abonnement) {
        return abonnementService.addAbonnement(abonnement);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    public Abonnement updatePlan(@PathVariable Long id, @RequestBody Abonnement abonnement) {
        abonnement.setId(id);
        return abonnementService.updateAbonnement(abonnement);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlan(@PathVariable Long id) {
        abonnementService.deleteAbonnement(id);
    }

    @GetMapping("/{id}")
    public Abonnement getPlanById(@PathVariable Long id) {
        return abonnementService.getAbonnementById(id);
    }

    @GetMapping
    public List<Abonnement> getAllPlans() {
        return abonnementService.getAllAbonnements();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // User subscription lifecycle
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Subscribe a user to a plan.
     * Result includes whether the subscription is ACTIVATED_NOW or QUEUED_NEXT.
     */
    @PostMapping("/users/{userId}/subscribe")
    public SubscribeResponseDTO subscribe(@PathVariable Long userId,
                                          @RequestParam Long abonnementId) {
        return abonnementService.subscribe(userId, abonnementId);
    }

    /**
     * Get all subscriptions for a user (sorted by dateDebut asc), including status.
     */
    @GetMapping("/users/{userId}/all")
    public List<UserAbonnementDTO> getUserSubscriptions(@PathVariable Long userId) {
        return abonnementService.getUserAbonnements(userId);
    }

    /**
     * Get the subscription timeline for a user:
     * current active, next queued, all queued, and expired history.
     */
    @GetMapping("/users/{userId}/timeline")
    public SubscriptionTimelineDTO getTimeline(@PathVariable Long userId) {
        return abonnementService.getTimeline(userId);
    }

    /**
     * Get (or generate) the QR code token for the user's active subscription.
     */
    @GetMapping("/users/{userId}/qrcode")
    public String getQRCode(@PathVariable Long userId) {
        return abonnementService.getQRCode(userId);
    }

    /**
     * Scan (validate) a QR code — decrements ticket count on the active subscription.
     */
    @PostMapping("/scan")
    public String scanQRCode(@RequestParam String code) {
        return abonnementService.scanQRCode(code);
    }
}
