package org.example.rawabet.services;

import jakarta.servlet.http.HttpServletRequest;
import org.example.rawabet.dto.AuthResponse;
import org.example.rawabet.dto.LoginRequest;
import org.example.rawabet.dto.SuspectLoginAlertRequest;
import org.example.rawabet.entities.User;

public interface IAuthService {

    /** Login avec comptage des tentatives */
    AuthResponse login(LoginRequest request, HttpServletRequest httpRequest);

    /** Génère un token d'impersonation CLIENT pour un admin */
    AuthResponse impersonate(Long targetUserId);

    /** Envoie un email d'alerte au SUPER_ADMIN avec photo */
    void handleSuspectLoginAlert(SuspectLoginAlertRequest request);

    void logout();

    User getAuthenticatedUser();

    void forgotPassword(String email);

    void validateResetToken(String token);

    void resetPassword(String token, String newPassword);

    void resetPasswordWithOtp(String email, String code, String newPassword);

    void verifyEmail(String token);
}