package org.example.rawabet.controllers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.rawabet.dto.UserSubscriptionResponse;
import org.example.rawabet.entities.Abonnement;
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

    @GetMapping
    public List<Abonnement> getAll() {
        return abonnementService.getAllAbonnements();
    }

    @PostMapping("/subscribe")
    @PreAuthorize("isAuthenticated()")
    public UserSubscriptionResponse subscribe(
            @RequestParam Long userId,
            @RequestParam Long abonnementId
    ) {
        return abonnementService.subscribe(userId, abonnementId);
    }

    @PostMapping("/subscribe/{userId}/{abonnementId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserSubscriptionResponse> subscribeWithPathVariables(
            @PathVariable Long userId,
            @PathVariable Long abonnementId
    ) {
        return ResponseEntity.ok(abonnementService.subscribe(userId, abonnementId));
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("isAuthenticated()")
    public UserSubscriptionResponse getSubscriptionByUserId(@PathVariable Long userId) {
        return abonnementService.getSubscriptionByUserId(userId);
    }

    @GetMapping("/qr/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> getQRCode(@PathVariable Long userId) {
        return ResponseEntity.ok(abonnementService.getQRCodeByUserId(userId));
    }

    @PostMapping("/scan")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> scanQRCode(@RequestBody ScanRequest request) {
        return ResponseEntity.ok(abonnementService.scanQRCode(request.getQrCode()));
    }

    @DeleteMapping("/users/expired")
    public long cleanupExpiredUserAbonnements() {
        return abonnementService.cleanupExpiredUserAbonnements();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ScanRequest {
        private String qrCode;
    }
}
