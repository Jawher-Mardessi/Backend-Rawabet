package org.example.rawabet.controllers;

import lombok.AllArgsConstructor;
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

    @DeleteMapping("/users/expired")
    public long cleanupExpiredUserAbonnements() {
        return abonnementService.cleanupExpiredUserAbonnements();
    }

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
