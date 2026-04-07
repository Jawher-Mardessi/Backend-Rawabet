package org.example.rawabet.controllers;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.AuthResponse;
import org.example.rawabet.dto.LoginRequest;
import org.example.rawabet.services.AuthServiceImpl;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthServiceImpl authService;
    public AuthController(AuthServiceImpl authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
    @PostMapping("/test")
    public String test() {
        return "OK";
    }

    @PostMapping("/logout")
    public String logout() {
        // côté client → supprimer le token du localStorage/cookie
        return "Logged out successfully";
    }
    // =========================
// 🔐 FORGOT PASSWORD
// =========================
    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email) {
        authService.forgotPassword(email);
        return "✅ Email de réinitialisation envoyé à " + email;
    }

    // =========================
// 🔐 RESET PASSWORD
// =========================
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token,
                                @RequestParam String newPassword) {
        authService.resetPassword(token, newPassword);
        return "✅ Mot de passe réinitialisé avec succès !";
    }
    // =========================
// ✅ VERIFY EMAIL
// =========================
    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return "✅ Email vérifié avec succès — vous pouvez maintenant vous connecter !";
    }
}