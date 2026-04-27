package org.example.rawabet.controllers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.rawabet.dto.SubscribeResponse;
import org.example.rawabet.dto.SubscriptionDto;
import org.example.rawabet.dto.TimelineResponse;
import org.example.rawabet.dto.UserSubscriptionResponse;
import org.example.rawabet.entities.Abonnement;
import org.example.rawabet.entities.UserAbonnement;
import org.example.rawabet.services.AbonnementServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/abonnements")
@RequiredArgsConstructor
public class AbonnementController {
    private final AbonnementServiceImpl abonnementService;

    // ==================== Abonnement Management ====================

    @GetMapping("/all")
    public List<UserAbonnement> getAll() {
        return abonnementService.getAllAbonnements();
    }

    // ==================== Subscribe ====================

    /**
     * Subscribe a user to an abonnement.
     * Returns detailed response with status and resultType (ACTIVATED_NOW or QUEUED_NEXT).
     */
    @PostMapping("/subscribe")
    @PreAuthorize("isAuthenticated()")
    public SubscribeResponse subscribe(
            @RequestParam Long userId,
            @RequestParam Long abonnementId
    ) {
        return abonnementService.subscribe(userId, abonnementId);
    }

    @PostMapping("/subscribe/{userId}/{abonnementId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SubscribeResponse> subscribeWithPathVariables(
            @PathVariable Long userId,
            @PathVariable Long abonnementId
    ) {
        return ResponseEntity.ok(abonnementService.subscribe(userId, abonnementId));
    }

    // ==================== Timeline Endpoint ====================

    /**
     * Get comprehensive timeline for a user's subscriptions.
     * Includes current, next queued, all queued, and history.
     */
    @GetMapping("/users/{userId}/timeline")
    @PreAuthorize("isAuthenticated()")
    public TimelineResponse getTimelineForUser(@PathVariable Long userId) {
        return abonnementService.getTimelineForUser(userId);
    }

    // ==================== User Subscriptions ====================

    /**
     * Get all subscriptions for a specific user, sorted by dateDebut ascending.
     * Each subscription includes its status.
     */
    @GetMapping("/users/{userId}/all")
    @PreAuthorize("isAuthenticated()")
    public List<SubscriptionDto> getAllUserSubscriptions(@PathVariable Long userId) {
        return abonnementService.getUserSubscriptions(userId);
    }

    @GetMapping("/users/{userId}/abonnements")
    @PreAuthorize("isAuthenticated()")
    public List<Abonnement> getUserAbonnementsList(@PathVariable Long userId) {
        return abonnementService.getAbonnementsByUserId(userId);
    }

    @DeleteMapping("/subscriptions/{subscriptionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteSubscriptionById(@PathVariable Long subscriptionId) {
        abonnementService.deleteSubscriptionById(subscriptionId);
        return ResponseEntity.noContent().build();
    }

    // ==================== Legacy Endpoints (Backward Compatibility) ====================

    /**
     * DEPRECATED: Use /users/{userId}/timeline instead.
     */
    @Deprecated
    @GetMapping("/users/{userId}")
    @PreAuthorize("isAuthenticated()")
    public UserSubscriptionResponse getSubscriptionByUserId(@PathVariable Long userId) {
        return abonnementService.getSubscriptionByUserId(userId);
    }

    // ==================== QR Code Management ====================

    @GetMapping("/qr/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getQRCode(@PathVariable Long userId) {
        try {
            String qrCode = abonnementService.getQRCodeByUserId(userId);
            return ResponseEntity.ok(new QRResponse(
                    qrCode,
                    "https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=" + qrCode
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Scan a QR code and consume a ticket.
     * Requires the subscription to be ACTIVE.
     */
    @PostMapping("/scan")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> scanQRCode(@RequestBody ScanRequest request) {
        try {
            AbonnementServiceImpl.ScanQRResponse response = abonnementService.scanQRCode(request.getQrCode());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ErrorResponse("Server error: " + e.getMessage()));
        }
    }

    // ==================== Cleanup (Deprecated) ====================

    /**
     * DEPRECATED: Subscriptions are no longer deleted.
     * This endpoint now just refreshes statuses.
     */
    @Deprecated
    @DeleteMapping("/users/expired")
    public long cleanupExpiredUserAbonnements() {
        return abonnementService.cleanupExpiredUserAbonnements();
    }

    // ==================== Response DTOs ====================

    @Getter
    @AllArgsConstructor
    public static class QRResponse {
        private String qrCode;
        private String imageUrl;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ScanRequest {
        private String qrCode;
    }

    @Getter
    @AllArgsConstructor
    public static class ErrorResponse {
        private String error;
    }
}
