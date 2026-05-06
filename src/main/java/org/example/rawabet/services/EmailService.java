package org.example.rawabet.services;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.utils.QRCodeGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import java.util.Base64;

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

    public void sendReservationConfirmedEmail(String toEmail, String reservationTitle, Long reservationId) {
        try {
            // Generate QR code that links to marking reservation as used
            String qrCodeUrl = frontendUrl + "/reservations/mark-used/" + reservationId;
            System.out.println("[EmailService] QR Code URL: " + qrCodeUrl);
            
            // Get QR code as PNG bytes (not Base64)
            byte[] qrCodeImageBytes = QRCodeGenerator.generateQRCodeAsBytes(qrCodeUrl, 300, 300);
            
            if (qrCodeImageBytes == null || qrCodeImageBytes.length == 0) {
                System.err.println("[EmailService] WARNING: QR code bytes are empty!");
            } else {
                System.out.println("[EmailService] QR code bytes length: " + qrCodeImageBytes.length);
            }

            // Create HTML email with QR code embedded as attachment
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("✅ Rawabet — Réservation confirmée");

            // Build HTML content. We'll include the embedded CID image (preferred) and a Base64 data-URI
            // fallback in case some mail clients strip inline attachments.
            String base64Image = null;
            try {
                base64Image = Base64.getEncoder().encodeToString(qrCodeImageBytes);
            } catch (Exception e) {
                System.err.println("[EmailService] Failed to Base64-encode QR code bytes: " + e.getMessage());
            }

            String dataUriImg = (base64Image != null) ? ("data:image/png;base64," + base64Image) : null;

            // Use Content-ID to reference the embedded image and include a Base64 fallback below
            StringBuilder htmlBuilder = new StringBuilder();
            htmlBuilder.append("<html>");
            htmlBuilder.append("<body style=\"font-family: Arial, sans-serif; color: #333;\">");
            htmlBuilder.append("<h2>✅ Reservation Confirmed</h2>");
            htmlBuilder.append("<p>Hello,</p>");
            htmlBuilder.append("<p>Your reservation has been successfully confirmed.</p>");
            htmlBuilder.append("<p><strong>Reservation:</strong> ").append(reservationTitle).append("</p>");
            htmlBuilder.append("<hr>");
            htmlBuilder.append("<h3>Please note: this QR code is your ticket for entry to the event, so please do not attempt to use it twice.</h3>");

            // CID image (preferred)
            htmlBuilder.append("<div>");
            htmlBuilder.append("<img src='cid:qrCode' alt='QR Code' width='300' height='300' style='border: 1px solid #ccc; padding: 10px; display:block;'>");
            htmlBuilder.append("</div>");

            // Base64 fallback (some clients may not support data URIs; it's a best-effort fallback)
            if (dataUriImg != null) {
                htmlBuilder.append("<p style=\"margin-top:10px;font-size:12px;color:#666;\">Si l'image ne s'affiche pas, utilisez ce QR (fallback) :</p>");
                htmlBuilder.append("<div>");
                htmlBuilder.append("<img src='").append(dataUriImg).append("' alt='QR Code' width='300' height='300' style='border: 1px solid #ccc; padding: 10px; display:block;'>");
                htmlBuilder.append("</div>");
            } else {
                htmlBuilder.append("<p style=\"margin-top:10px;font-size:12px;color:#666;\">Si l'image ne s'affiche pas, ouvrez ce lien: <a href=\"").append(qrCodeUrl).append("\">Marquer réservation</a></p>");
            }

            htmlBuilder.append("<p style=\"margin-top: 20px; font-size: 12px; color: #666;\">L'équipe Rawabet</p>");
            htmlBuilder.append("</body>");
            htmlBuilder.append("</html>");

            String htmlContent = htmlBuilder.toString();
            helper.setText(htmlContent, true);

            // Add QR code as inline attachment with Content-ID (preferred method)
            ByteArrayResource qrImageResource = new ByteArrayResource(qrCodeImageBytes);
            helper.addInline("qrCode", qrImageResource, "image/png");

            System.out.println("[EmailService] Sending reservation confirmation email to: " + toEmail);
            mailSender.send(message);
            System.out.println("[EmailService] Email sent successfully");
            
        } catch (MessagingException e) {
            System.err.println("[EmailService] MessagingException: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send reservation confirmation email: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("[EmailService] Unexpected error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send reservation confirmation email: " + e.getMessage(), e);
        }
    }

    // Legacy method for backward compatibility
    public void sendReservationConfirmedEmail(String toEmail, String reservationTitle) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("✅ Rawabet — Réservation confirmée");
        message.setText(
                "Bonjour,\n\n" +
                        "Votre réservation a été confirmée avec succès.\n\n" +
                        "Réservation : " + reservationTitle + "\n\n" +
                        "L'équipe Rawabet"
        );

        mailSender.send(message);
    }
}