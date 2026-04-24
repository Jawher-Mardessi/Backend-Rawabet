package org.example.rawabet.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.AdminActivityEvent;
import org.example.rawabet.dto.AuthResponse;
import org.example.rawabet.dto.LoginRequest;
import org.example.rawabet.dto.SuspectLoginAlertRequest;
import org.example.rawabet.entities.EmailVerificationToken;
import org.example.rawabet.entities.PasswordResetToken;
import org.example.rawabet.entities.User;
import org.example.rawabet.repositories.EmailVerificationTokenRepository;
import org.example.rawabet.repositories.PasswordResetTokenRepository;
import org.example.rawabet.repositories.UserRepository;
import org.example.rawabet.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private static final int OTP_LENGTH           = 6;
    private static final int MAX_OTP_ATTEMPTS     = 5;
    /** Nombre de tentatives avant alerte caméra */
    private static final int MAX_LOGIN_ATTEMPTS   = 5;
    /** Durée de blocage après MAX_LOGIN_ATTEMPTS (minutes) */
    private static final int LOCKOUT_MINUTES      = 15;

    private final UserRepository                     userRepository;
    private final PasswordEncoder                    passwordEncoder;
    private final JwtService                         jwtService;
    private final PasswordResetTokenRepository       resetTokenRepository;
    private final EmailService                       emailService;
    private final EmailVerificationTokenRepository   verificationTokenRepository;
    private final AdminActivityPublisher             activityPublisher;  // ← AJOUT

    @Value("${app.super-admin.email:amennahali8@gmail.com}")
    private String superAdminEmail;

    // ────────────────────────────────────────────────────────────────────────
    // 🔐 LOGIN — comptage des tentatives échouées + verrouillage
    // ────────────────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {

        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // ── 1. Verrouillage temporaire ? ───────────────────────────────────
        if (user.isLoginLocked()) {
            long secondsLeft = java.time.Duration.between(
                    LocalDateTime.now(), user.getLoginLockedUntil()).getSeconds();
            throw new RuntimeException(
                    "Compte temporairement verrouillé. Réessayez dans " + secondsLeft + "s.");
        }

        // ── 2. Banni ? ─────────────────────────────────────────────────────
        checkBanStatus(user);

        // ── 3. Vérification mot de passe ───────────────────────────────────
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            handleFailedAttempt(user);
            int remaining = MAX_LOGIN_ATTEMPTS - user.getLoginFailedAttempts();
            if (remaining > 0) {
                throw new RuntimeException(
                        "Invalid credentials (" + remaining + " tentative(s) restante(s))");
            } else {
                throw new RuntimeException(
                        "Trop de tentatives. Compte verrouillé " + LOCKOUT_MINUTES + " minutes. " +
                                "Votre photo a été capturée et envoyée à l'administrateur.");
            }
        }

        // ── 4. Succès — réinitialiser le compteur ──────────────────────────
        user.setLoginFailedAttempts(0);
        user.setLoginLockedUntil(null);
        userRepository.save(user);

        String token = jwtService.generateToken(user);
        activityPublisher.publish(AdminActivityEvent.userLogin(user.getEmail())); // ← AJOUT
        return new AuthResponse(token);
    }

    /**
     * Incrémente le compteur d'échecs et verrouille si MAX atteint.
     */
    private void handleFailedAttempt(User user) {
        int attempts = user.getLoginFailedAttempts() + 1;
        user.setLoginFailedAttempts(attempts);

        if (attempts >= MAX_LOGIN_ATTEMPTS) {
            user.setLoginLockedUntil(LocalDateTime.now().plusMinutes(LOCKOUT_MINUTES));
        }

        userRepository.save(user);
    }

    /**
     * Vérifie si l'utilisateur est banni (permanent ou temporaire).
     * Si le ban temporaire a expiré, réactive le compte automatiquement.
     */
    private void checkBanStatus(User user) {
        if (!user.isActive()) {
            if (user.getBanUntil() != null && LocalDateTime.now().isAfter(user.getBanUntil())) {
                user.setActive(true);
                user.setBanUntil(null);
                user.setBanReason(null);
                user.setTokenVersion(user.getTokenVersion() + 1);
                userRepository.save(user);
            } else {
                String msg = "Compte désactivé — contactez l'administrateur";
                if (user.getBanUntil() != null) {
                    msg = "Compte suspendu jusqu'au " + user.getBanUntil() + ". Raison : " +
                            (user.getBanReason() != null ? user.getBanReason() : "non spécifiée");
                }
                throw new RuntimeException(msg);
            }
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // 📸 ALERTE TENTATIVE SUSPECTE — envoi email SUPER_ADMIN avec photo
    // ────────────────────────────────────────────────────────────────────────
    @Override
    public void handleSuspectLoginAlert(SuspectLoginAlertRequest request) {
        emailService.sendSuspectLoginAlert(
                superAdminEmail,
                request.getEmail(),
                request.getTimestamp(),
                request.getClientIp(),
                request.getPhotoBase64()
        );
        activityPublisher.publish(AdminActivityEvent.suspectLogin(request.getEmail(), request.getClientIp())); // ← AJOUT
    }

    // ────────────────────────────────────────────────────────────────────────
    // 🎭 IMPERSONATION — génère un token CLIENT pour un admin
    // ────────────────────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public AuthResponse impersonate(Long targetUserId) {

        User admin = getAuthenticatedUser();

        boolean isAdmin = admin.getRoles().stream()
                .anyMatch(r -> r.getName().equals("SUPER_ADMIN")
                        || r.getName().equals("ADMIN_CINEMA")
                        || r.getName().equals("ADMIN_EVENT")
                        || r.getName().equals("ADMIN_CLUB"));
        if (!isAdmin) {
            throw new RuntimeException("Action non autorisée");
        }

        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("Utilisateur cible introuvable"));

        boolean targetIsAdmin = target.getRoles().stream()
                .anyMatch(r -> r.getName().equals("SUPER_ADMIN")
                        || r.getName().equals("ADMIN_CINEMA")
                        || r.getName().equals("ADMIN_EVENT")
                        || r.getName().equals("ADMIN_CLUB"));

        if (targetIsAdmin && !target.getId().equals(admin.getId())) {
            throw new RuntimeException("Impersonation d'un autre admin interdite");
        }

        String token = jwtService.generateImpersonationToken(target, admin.getId());
        return new AuthResponse(token);
    }

    // ────────────────────────────────────────────────────────────────────────
    // 🔐 LOGOUT
    // ────────────────────────────────────────────────────────────────────────
    @Override
    public void logout() {
        // stateless JWT — rien côté serveur
    }

    // ────────────────────────────────────────────────────────────────────────
    // 🔐 GET AUTHENTICATED USER
    // ────────────────────────────────────────────────────────────────────────
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

    // ────────────────────────────────────────────────────────────────────────
    // 🔐 FORGOT PASSWORD
    // ────────────────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email.trim().toLowerCase()).orElse(null);
        if (user == null) return;

        String otpCode  = generateOtpCode();
        String otpHash  = hashToken(otpCode);

        PasswordResetToken resetToken = resetTokenRepository.findByUser(user)
                .orElseGet(PasswordResetToken::new);
        resetToken.setToken(otpHash);
        resetToken.setUser(user);
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        resetToken.setUsed(false);
        resetToken.setFailedAttempts(0);

        resetTokenRepository.save(resetToken);
        emailService.sendPasswordResetOtpEmail(email, otpCode);
    }

    @Override
    @Transactional(readOnly = true)
    public void validateResetToken(String token) {
        String tokenHash = hashToken(token);
        PasswordResetToken resetToken = resetTokenRepository.findByToken(tokenHash)
                .orElseThrow(() -> new RuntimeException("Token invalide"));

        if (resetToken.isExpired()) throw new RuntimeException("Token expiré");
        if (resetToken.isUsed())    throw new RuntimeException("Token déjà utilisé");
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        String tokenHash  = hashToken(token);
        PasswordResetToken resetToken = resetTokenRepository.findByToken(tokenHash)
                .orElseThrow(() -> new RuntimeException("Token invalide"));

        if (resetToken.isExpired()) throw new RuntimeException("Token expiré");
        if (resetToken.isUsed())    throw new RuntimeException("Token déjà utilisé");

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setTokenVersion(user.getTokenVersion() + 1);
        userRepository.save(user);

        resetToken.setUsed(true);
        resetTokenRepository.save(resetToken);
    }

    @Override
    @Transactional
    public void resetPasswordWithOtp(String email, String code, String newPassword) {
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Code invalide ou expiré"));

        PasswordResetToken resetToken = resetTokenRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Code invalide ou expiré"));

        if (resetToken.isExpired())                                      throw new RuntimeException("Code expiré");
        if (resetToken.isUsed())                                         throw new RuntimeException("Code déjà utilisé");
        if (resetToken.getFailedAttempts() >= MAX_OTP_ATTEMPTS)          throw new RuntimeException("Trop de tentatives");

        if (!hashToken(code).equals(resetToken.getToken())) {
            resetToken.setFailedAttempts(resetToken.getFailedAttempts() + 1);
            resetTokenRepository.save(resetToken);
            throw new RuntimeException("Code invalide");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setTokenVersion(user.getTokenVersion() + 1);
        userRepository.save(user);

        resetToken.setUsed(true);
        resetTokenRepository.save(resetToken);
    }

    // ────────────────────────────────────────────────────────────────────────
    // ✅ VERIFY EMAIL
    // ────────────────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public void verifyEmail(String token) {
        EmailVerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token invalide"));

        if (verificationToken.isExpired()) throw new RuntimeException("Token expiré");

        User user = verificationToken.getUser();
        user.setActive(true);
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);
    }

    // ────────────────────────────────────────────────────────────────────────
    // Private helpers
    // ────────────────────────────────────────────────────────────────────────
    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashed) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Hash algorithm unavailable", e);
        }
    }

    private String generateOtpCode() {
        SecureRandom sr = new SecureRandom();
        return String.format("%0" + OTP_LENGTH + "d", sr.nextInt(1_000_000));
    }
}