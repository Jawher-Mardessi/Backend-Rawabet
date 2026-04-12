package org.example.rawabet.services;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.AuthResponse;
import org.example.rawabet.dto.LoginRequest;
import org.example.rawabet.entities.EmailVerificationToken;
import org.example.rawabet.entities.PasswordResetToken;
import org.example.rawabet.entities.User;
import org.example.rawabet.repositories.EmailVerificationTokenRepository;
import org.example.rawabet.repositories.PasswordResetTokenRepository;
import org.example.rawabet.repositories.UserRepository;
import org.example.rawabet.security.JwtService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final EmailService emailService;
    private final EmailVerificationTokenRepository verificationTokenRepository;

    // 🔐 LOGIN
    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ vérifier si banni
        if (!user.isActive()) {
            throw new RuntimeException("Compte désactivé — contactez l'administrateur");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    // 🔐 LOGOUT
    @Override
    public void logout() {
        // stateless JWT — rien côté serveur
    }

    // 🔐 GET AUTHENTICATED USER
    @Override
    public User getAuthenticatedUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof User)) {
            throw new RuntimeException("Invalid authentication principal");
        }

        return (User) principal;
    }

    // =========================
// 🔐 FORGOT PASSWORD
// =========================
    @Override
    public void forgotPassword(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email non trouvé"));

        // ✅ supprimer ancien token si existe
        resetTokenRepository.deleteByUser(user);

        // ✅ générer token unique
        String rawToken = UUID.randomUUID().toString();
        String tokenHash = hashToken(rawToken);

        // ✅ sauvegarder uniquement le hash du token
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(tokenHash);
        resetToken.setUser(user);
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        resetToken.setUsed(false);

        resetTokenRepository.save(resetToken);

        // ✅ envoyer email
        emailService.sendPasswordResetEmail(email, rawToken);
    }

    // =========================
// 🔐 RESET PASSWORD
// =========================
    @Override
    public void resetPassword(String token, String newPassword) {

        String tokenHash = hashToken(token);

        PasswordResetToken resetToken = resetTokenRepository.findByToken(tokenHash)
                .orElseThrow(() -> new RuntimeException("Token invalide"));

        // ✅ vérifier expiration
        if (resetToken.isExpired()) {
            throw new RuntimeException("Token expiré — demandez un nouveau lien");
        }

        // ✅ vérifier déjà utilisé
        if (resetToken.isUsed()) {
            throw new RuntimeException("Token déjà utilisé");
        }

        // ✅ changer mot de passe
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setTokenVersion(user.getTokenVersion() + 1);
        userRepository.save(user);

        // ✅ marquer token comme utilisé
        resetToken.setUsed(true);
        resetTokenRepository.save(resetToken);
    }

    // =========================
// ✅ VERIFY EMAIL
// =========================
    @Override
    public void verifyEmail(String token) {

        EmailVerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token invalide"));

        // ✅ vérifier expiration
        if (verificationToken.isExpired()) {
            throw new RuntimeException("Token expiré — demandez un nouveau lien");
        }

        // ✅ activer le compte
        User user = verificationToken.getUser();
        user.setActive(true);
        userRepository.save(user);

        // ✅ supprimer le token
        verificationTokenRepository.delete(verificationToken);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashed) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Hash algorithm unavailable", e);
        }
    }
}