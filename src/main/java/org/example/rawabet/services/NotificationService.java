package org.example.rawabet.services;

import org.example.rawabet.entities.Notification;
import org.example.rawabet.entities.User;
import org.example.rawabet.enums.NotificationType;
import org.example.rawabet.repositories.NotificationRepository;
import org.example.rawabet.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final String mailFrom;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository,
                               JavaMailSender mailSender,
                               @Value("${spring.mail.username:}") String mailFrom) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
        this.mailFrom = mailFrom;
    }

    // =====================
    // Gestion notifications
    // =====================

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification non trouvée"));
    }

    public void deleteNotification(Long id) {
        Notification notif = getNotificationById(id);
        notificationRepository.delete(notif);
    }

    public Notification markAsRead(Long id) {
        Notification notif = getNotificationById(id);
        notif.setLue(true);
        return notificationRepository.save(notif); // update en DB
    }

    // =====================
    // Gestion utilisateurs
    // =====================

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
    }

    // =====================
    // Notifications Push
    // =====================

    public Notification createNotifPush(User user, String message) {
        Notification notification = buildNotification(user, message, NotificationType.PUSH);
        return notificationRepository.save(notification);
    }

    // =====================
    // Notifications Email
    // =====================

    public Notification createNotifEmail(User user, String message) {
        Notification notification = buildNotification(user, message, NotificationType.EMAIL);
        Notification savedNotification = notificationRepository.save(notification);

        try {
            sendEmail(user.getEmail(), message);
        } catch (MailException | IllegalArgumentException e) {
            logger.error("Echec d'envoi de l'email pour l'utilisateur {}: {}", user.getId(), e.getMessage());
        }

        return savedNotification;
    }

    public void sendEmail(String email, String message) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("L'adresse email est invalide");
        }

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        if (mailFrom != null && !mailFrom.isBlank()) {
            mailMessage.setFrom(mailFrom);
        }
        mailMessage.setTo(email);
        mailMessage.setSubject("Nouvelle notification");
        mailMessage.setText(message);
        mailSender.send(mailMessage);
    }

    // =====================
    // Méthode utilitaire
    // =====================

    private Notification buildNotification(User user, String message, NotificationType type) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setType(type);
        notification.setDateEnvoi(LocalDateTime.now());
        notification.setLue(false); // par défaut non lue
        return notification;
    }
}
