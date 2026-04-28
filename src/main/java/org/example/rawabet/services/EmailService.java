package org.example.rawabet.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public void sendPasswordResetEmail(String toEmail, String token) {

        String resetLink = frontendUrl + "/reset-password?token=" + token;

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

    public void sendWarningEmail(String toEmail, String userName, String commentaire) {
        try {
            log.info("Tentative envoi mail vers {}", toEmail);
            log.info("Expediteur configure: {}", fromEmail);

            if (toEmail == null || toEmail.isBlank()) {
                throw new RuntimeException("Email destinataire vide");
            }

            String resolvedUserName = userName == null || userName.isBlank() ? "utilisateur" : userName;
            String resolvedCommentaire = commentaire == null ? "" : commentaire;

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Avertissement moderation Rawabet");
            helper.setText(buildWarningEmailHtml(resolvedUserName, resolvedCommentaire), true);

            mailSender.send(mimeMessage);

            log.info("Mail envoye avec succes vers {}", toEmail);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi du mail: {}", e.getMessage(), e);
            throw new RuntimeException("Echec envoi mail", e);
        }
    }

    private String buildWarningEmailHtml(String userName, String commentaire) {
        String safeUserName = escapeHtml(userName);
        String safeCommentaire = escapeHtml(commentaire);

        String template = """
                <!doctype html>
                <html lang="fr">
                <head>
                  <meta charset="UTF-8" />
                  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                  <title>Avertissement moderation Rawabet</title>
                </head>
                <body style="margin:0;padding:0;background:#f3f4f6;font-family:Arial,sans-serif;color:#1f2937;">
                  <table role="presentation" width="100%" cellspacing="0" cellpadding="0" style="padding:24px 12px;background:#f3f4f6;">
                    <tr>
                      <td align="center">
                        <table role="presentation" width="100%" cellspacing="0" cellpadding="0" style="max-width:680px;background:#ffffff;border:1px solid #e5e7eb;border-radius:14px;overflow:hidden;">
                          <tr>
                            <td style="padding:20px 24px;background:linear-gradient(90deg,#0f172a 0%,#1f2937 55%,#b45309 100%);color:#ffffff;">
                              <div style="font-size:11px;letter-spacing:0.14em;text-transform:uppercase;color:#fcd34d;font-weight:700;">Rawabet moderation</div>
                              <div style="margin-top:8px;font-size:24px;font-weight:700;line-height:1.25;">Avertissement de contenu</div>
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:24px;">
                              <p style="margin:0 0 14px 0;">Bonjour {{USER_NAME}},</p>
                              <p style="margin:0 0 14px 0;">
                                Nous vous ecrivons pour vous signaler que votre recent commentaire sur Rawabet
                                a ete detecte comme contenant des termes inappropries ou des mots interdits.
                              </p>

                              <div style="background:#fff1f2;border:1px solid #fecdd3;border-radius:12px;padding:14px;margin:0 0 14px 0;">
                                <div style="font-size:11px;letter-spacing:0.1em;text-transform:uppercase;color:#be123c;font-weight:700;">Votre commentaire concerne</div>
                                <div style="margin-top:8px;color:#881337;font-weight:700;">"{{COMMENTAIRE}}"</div>
                              </div>

                              <div style="background:#f8fafc;border:1px solid #e2e8f0;border-radius:12px;padding:14px;margin:0 0 14px 0;">
                                <div style="font-size:11px;letter-spacing:0.1em;text-transform:uppercase;color:#334155;font-weight:700;">Rappel des regles de la plateforme</div>
                                <ul style="margin:10px 0 0 18px;padding:0;">
                                  <li style="margin-bottom:6px;">Le respect entre les membres est obligatoire.</li>
                                  <li style="margin-bottom:6px;">Les insultes, propos vulgaires ou haineux sont interdits.</li>
                                  <li style="margin-bottom:6px;">Tout contenu inapproprie peut entrainer la suppression du commentaire.</li>
                                  <li>Des avertissements repetes peuvent conduire a la suspension de votre compte.</li>
                                </ul>
                              </div>

                              <div style="background:#fffbeb;border:1px solid #fde68a;border-radius:12px;padding:14px;margin:0 0 14px 0;">
                                <div style="font-size:11px;letter-spacing:0.1em;text-transform:uppercase;color:#92400e;font-weight:700;">Comment corriger la situation</div>
                                <ul style="margin:10px 0 0 18px;padding:0;">
                                  <li style="margin-bottom:6px;">Modifiez votre commentaire pour retirer les termes inappropries.</li>
                                  <li style="margin-bottom:6px;">Consultez notre charte d'utilisation pour plus d'informations.</li>
                                  <li>Contactez-nous si vous pensez qu'il s'agit d'une erreur.</li>
                                </ul>
                              </div>

                              <p style="margin:0;color:#6b7280;font-size:13px;">
                                Cet avertissement est automatique. Nous vous remercions de votre comprehension
                                et de votre contribution a une communaute respectueuse.
                              </p>
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:16px 24px;background:#f9fafb;border-top:1px solid #e5e7eb;color:#6b7280;font-size:12px;">
                              Equipe Rawabet
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """;

        return template
                .replace("{{USER_NAME}}", safeUserName)
                .replace("{{COMMENTAIRE}}", safeCommentaire);
    }

    private String escapeHtml(String value) {
        return String.valueOf(value)
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
