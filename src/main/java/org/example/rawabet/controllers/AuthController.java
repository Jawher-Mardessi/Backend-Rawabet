package org.example.rawabet.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.*;
import org.example.rawabet.services.AuthServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Duration;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private static final String RESET_COOKIE_NAME = "rawabet_reset_token";

    private final AuthServiceImpl authService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${app.cookie.secure:false}")
    private boolean secureCookie;

    // ────────────────────────────────────────────────────────────────
    // 🔐 LOGIN — avec comptage des tentatives échouées
    // ────────────────────────────────────────────────────────────────
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        return authService.login(request, httpRequest);
    }

    @PostMapping("/test")
    public String test() {
        return "OK";
    }

    @PostMapping("/logout")
    public String logout() {
        return "Logged out successfully";
    }

    // ────────────────────────────────────────────────────────────────
    // 📸 ALERTE TENTATIVE SUSPECTE
    // Appelé par le frontend après 5 échecs de connexion
    // ────────────────────────────────────────────────────────────────
    @PostMapping("/suspect-alert")
    public ResponseEntity<String> suspectLoginAlert(
            @RequestBody SuspectLoginAlertRequest request,
            HttpServletRequest httpRequest) {

        // Récupérer l'IP réelle (proxy-aware)
        String realIp = getClientIp(httpRequest);
        if (request.getClientIp() == null || request.getClientIp().isBlank()) {
            request.setClientIp(realIp);
        }

        authService.handleSuspectLoginAlert(request);
        return ResponseEntity.ok("Alerte enregistrée");
    }

    // ────────────────────────────────────────────────────────────────
    // 🔐 FORGOT / RESET PASSWORD
    // ────────────────────────────────────────────────────────────────
    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email) {
        authService.forgotPassword(email);
        return "✅ Si cet email existe, un code OTP a été envoyé.";
    }

    @GetMapping("/reset-password/confirm")
    public ResponseEntity<Void> confirmResetPasswordToken(@RequestParam String token) {
        authService.validateResetToken(token);

        ResponseCookie cookie = ResponseCookie.from(RESET_COOKIE_NAME, token)
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofMinutes(15))
                .build();

        return ResponseEntity.status(302)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .location(URI.create(frontendUrl + "/auth/reset-password"))
                .build();
    }

    @PostMapping("/reset-password/session")
    public String resetPasswordWithSession(
            @CookieValue(name = RESET_COOKIE_NAME, required = false) String token,
            @RequestBody @Valid ResetPasswordRequest request,
            HttpServletResponse response) {

        if (token == null || token.isBlank()) {
            throw new RuntimeException("Session de réinitialisation invalide ou expirée");
        }

        authService.resetPassword(token, request.getNewPassword());

        ResponseCookie clearCookie = ResponseCookie.from(RESET_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, clearCookie.toString());
        return "✅ Mot de passe réinitialisé avec succès !";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token,
                                @RequestParam String newPassword) {
        authService.resetPassword(token, newPassword);
        return "✅ Mot de passe réinitialisé avec succès !";
    }

    @PostMapping("/reset-password/otp")
    public String resetPasswordWithOtp(@RequestBody @Valid ResetPasswordOtpRequest request) {
        authService.resetPasswordWithOtp(request.getEmail(), request.getCode(), request.getNewPassword());
        return "✅ Mot de passe réinitialisé avec succès !";
    }

    // ────────────────────────────────────────────────────────────────
    // ✅ VERIFY EMAIL
    // ────────────────────────────────────────────────────────────────
    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return "✅ Email vérifié avec succès — vous pouvez maintenant vous connecter !";
    }

    // ────────────────────────────────────────────────────────────────
    // Private helpers
    // ────────────────────────────────────────────────────────────────
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isBlank()) {
            return xRealIp.trim();
        }
        return request.getRemoteAddr();
    }
}