package org.example.rawabet.services;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    // ── OTP Reset Password ─────────────────────────────────────────────────
    @Async
    public void sendPasswordResetOtpEmail(String toEmail, String otpCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Rawabet — Reinitialisation de mot de passe");
        message.setText(
                "Bonjour,\n\n" +
                        "Vous avez demande une reinitialisation de votre mot de passe.\n\n" +
                        "Voici votre code OTP (6 chiffres) : " + otpCode + "\n\n" +
                        "Ce code expire dans 15 minutes et ne peut etre utilise qu'une seule fois.\n\n" +
                        "Si vous n'avez pas fait cette demande, ignorez cet email.\n\n" +
                        "L'equipe Rawabet"
        );
        mailSender.send(message);
    }

    // ── Verify Email ───────────────────────────────────────────────────────
    @Async
    public void sendVerificationEmail(String toEmail, String token) {
        String verifyLink = frontendUrl + "/verify-email?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Rawabet — Confirmez votre email");
        message.setText(
                "Bonjour,\n\n" +
                        "Merci de vous etre inscrit sur Rawabet !\n\n" +
                        "Cliquez sur ce lien pour confirmer votre email :\n" +
                        verifyLink + "\n\n" +
                        "Ce lien expire dans 24 heures.\n\n" +
                        "Si vous n'avez pas cree de compte, ignorez cet email.\n\n" +
                        "L'equipe Rawabet"
        );
        mailSender.send(message);
    }

    // ── Alerte tentative suspecte (photo en CID — compatible Gmail) ────────
    /**
     * Envoie la photo en piece jointe CID.
     * Gmail bloque les src="data:image/..." inline — la methode CID
     * (Content-ID) est la solution standard supportee par tous les clients mail.
     */
    @Async
    public void sendSuspectLoginAlert(
            String toSuperAdmin,
            String suspectEmail,
            String timestamp,
            String clientIp,
            String photoBase64) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toSuperAdmin);
            helper.setSubject("ALERTE SECURITE — Tentative suspecte sur Rawabet");

            String now = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

            boolean hasPhoto = photoBase64 != null
                    && photoBase64.startsWith("data:image")
                    && photoBase64.length() > 1000;

            log.info("[SECURITY] Alert received - email: {}, photoSize: {}, hasPhoto: {}",
                    suspectEmail,
                    photoBase64 != null ? photoBase64.length() : 0,
                    hasPhoto);

            String photoHtml = hasPhoto
                    ? "<div style='margin:16px 0;'>" +
                    "<p style='font-weight:bold;color:#dc2626;margin:0 0 8px;'>Photo capturee :</p>" +
                    "<img src='cid:suspectPhoto' alt='suspect' " +
                    "style='max-width:320px;border-radius:8px;border:2px solid #dc2626;display:block;'/>" +
                    "</div>"
                    : "<p style='color:#6b7280;font-style:italic;'>Photo non disponible.</p>";

            String html =
                    "<!DOCTYPE html><html><body style='font-family:sans-serif;background:#f9fafb;padding:24px;'>" +
                            "<div style='max-width:600px;margin:0 auto;background:#fff;border-radius:12px;" +
                            "border:2px solid #dc2626;overflow:hidden;'>" +
                            "<div style='background:#dc2626;padding:20px 24px;'>" +
                            "<h1 style='color:#fff;margin:0;font-size:20px;'>Alerte Securite — Rawabet</h1>" +
                            "<p style='color:#fecaca;margin:4px 0 0;font-size:14px;'>5 tentatives de connexion echouees</p>" +
                            "</div>" +
                            "<div style='padding:24px;'>" +
                            "<table style='width:100%;border-collapse:collapse;font-size:15px;'>" +
                            "<tr><td style='padding:10px 0;color:#6b7280;width:160px;'>Email utilise</td>" +
                            "<td style='padding:10px 0;font-weight:bold;color:#111;'>" + escapeHtml(suspectEmail) + "</td></tr>" +
                            "<tr><td style='padding:10px 0;color:#6b7280;'>Timestamp client</td>" +
                            "<td style='padding:10px 0;color:#111;'>" + escapeHtml(timestamp != null ? timestamp : "N/A") + "</td></tr>" +
                            "<tr><td style='padding:10px 0;color:#6b7280;'>Timestamp serveur</td>" +
                            "<td style='padding:10px 0;color:#111;'>" + now + "</td></tr>" +
                            "<tr><td style='padding:10px 0;color:#6b7280;'>Adresse IP</td>" +
                            "<td style='padding:10px 0;color:#111;'>" + escapeHtml(clientIp != null ? clientIp : "N/A") + "</td></tr>" +
                            "</table>" +
                            photoHtml +
                            "<div style='background:#fef2f2;border:1px solid #fecaca;border-radius:8px;padding:16px;margin-top:16px;'>" +
                            "<p style='margin:0;color:#991b1b;font-size:14px;'>" +
                            "Si ce comportement vous semble suspect, connectez-vous a l'interface admin " +
                            "pour bannir cet utilisateur ou bloquer cette adresse IP.</p>" +
                            "</div></div>" +
                            "<div style='background:#f3f4f6;padding:12px 24px;text-align:center;font-size:12px;color:#9ca3af;'>" +
                            "Rawabet — Systeme de securite automatique</div>" +
                            "</div></body></html>";

            helper.setText(html, true);

            // Attacher la photo en ressource inline CID
            if (hasPhoto) {
                String mimeType  = photoBase64.substring(5, photoBase64.indexOf(';'));
                String b64Data   = photoBase64.substring(photoBase64.indexOf(',') + 1);
                byte[] imageBytes = Base64.getDecoder().decode(b64Data);
                helper.addInline("suspectPhoto", new ByteArrayResource(imageBytes), mimeType);
            }

            mailSender.send(mimeMessage);

        } catch (Exception e) {
            log.error("Impossible d'envoyer l'alerte de securite : {}", e.getMessage(), e);
        }
    }

    // ── Notification ban temporaire ────────────────────────────────────────
    @Async
    public void sendBanNotification(String toEmail, String userName,
                                    LocalDateTime banUntil, String reason) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Rawabet — Votre compte a ete suspendu");

            String duration = banUntil != null
                    ? "jusqu'au " + banUntil.format(DateTimeFormatter.ofPattern("dd/MM/yyyy a HH:mm"))
                    : "de maniere permanente";

            message.setText(
                    "Bonjour " + userName + ",\n\n" +
                            "Votre compte Rawabet a ete suspendu " + duration + ".\n\n" +
                            "Raison : " + (reason != null ? reason : "Non specifiee") + "\n\n" +
                            (banUntil != null
                                    ? "Votre acces sera automatiquement retabli a la date indiquee.\n\n"
                                    : "Pour contester cette decision, contactez le support.\n\n") +
                            "L'equipe Rawabet"
            );
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Impossible d'envoyer la notification de ban a {} : {}", toEmail, e.getMessage());
        }
    }

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    // ── Alerte simple (utilisé par MlController pour les alertes churn) ───
    @Async
    public void sendSimpleAlert(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Impossible d'envoyer l'alerte simple : {}", e.getMessage());
        }
    }
}