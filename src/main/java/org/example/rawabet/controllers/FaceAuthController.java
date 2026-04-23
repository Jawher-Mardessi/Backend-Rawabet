package org.example.rawabet.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.rawabet.dto.AuthResponse;
import org.example.rawabet.dto.FaceLoginRequest;
import org.example.rawabet.dto.FaceRegisterRequest;
import org.example.rawabet.services.FaceAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth/face")
@RequiredArgsConstructor
public class FaceAuthController {

    private final FaceAuthService faceAuthService;

    // ─────────────────────────────────────────
    //  POST /auth/face/register
    //  Enregistre le visage d'un utilisateur
    //  → PUBLIC (appelé depuis le profil après login)
    // ─────────────────────────────────────────
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerFace(
            @RequestBody FaceRegisterRequest request) {

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email manquant"));
        }
        if (request.getImage() == null || request.getImage().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Image manquante"));
        }

        try {
            String message = faceAuthService.registerFace(
                    request.getEmail().trim(), request.getImage());
            return ResponseEntity.ok(Map.of("message", message));
        } catch (RuntimeException e) {
            log.warn("[FaceAuth] Register échoué : {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────
    //  POST /auth/face/login
    //  Authentifie par visage → retourne JWT Spring
    //  → PUBLIC (c'est une méthode de login, pas de token requis)
    // ─────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<?> loginWithFace(
            @RequestBody FaceLoginRequest request) {

        if (request.getImage() == null || request.getImage().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Image manquante"));
        }

        try {
            AuthResponse authResponse = faceAuthService.loginWithFace(request.getImage());
            return ResponseEntity.ok(authResponse);
        } catch (RuntimeException e) {
            log.warn("[FaceAuth] Login visage échoué : {}", e.getMessage());
            return ResponseEntity.status(401)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> faceStatus(@RequestParam String email) {
        boolean exists = faceAuthService.hasFaceRegistered(email);
        return ResponseEntity.ok(Map.of("registered", exists));
    }
}