package org.example.rawabet.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public void sendPasswordResetEmail(String toEmail, String token) {

        String resetLink = frontendUrl + "/auth/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("🔐 Rawabet — Réinitialisation de mot de passe");
        message.setText(
                "Bonjour,\n\n" +
                        "Vous avez demandé une réinitialisation de votre mot de passe.\n\n" +
                        "Cliquez sur ce lien pour réinitialiser votre mot de passe :\n" +
                        resetLink + "\n\n" +
                        "Ce lien expire dans 15 minutes.\n\n" +
                        "Si vous n'avez pas fait cette demande, ignorez cet email.\n\n" +
                        "L'équipe Rawabet"
        );

        mailSender.send(message);
    }

    public void sendVerificationEmail(String toEmail, String token) {

        String verifyLink = frontendUrl + "/verify-email?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("✅ Rawabet — Confirmez votre email");
        message.setText(
                "Bonjour,\n\n" +
                        "Merci de vous être inscrit sur Rawabet !\n\n" +
                        "Cliquez sur ce lien pour confirmer votre email :\n" +
                        verifyLink + "\n\n" +
                        "Ce lien expire dans 24 heures.\n\n" +
                        "Si vous n'avez pas créé de compte, ignorez cet email.\n\n" +
                        "L'équipe Rawabet"
        );

        mailSender.send(message);
    }
}