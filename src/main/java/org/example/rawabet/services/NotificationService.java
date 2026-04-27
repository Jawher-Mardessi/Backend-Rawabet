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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public List<Notification> getNotificationsByUserId(Long userId) {
        getUserById(userId);
        return notificationRepository.findByUser_Id(userId);
    }

    public List<Notification> getUnreadNotificationsByUserId(Long userId) {
        getUserById(userId);
        return notificationRepository.findByUser_IdAndLueFalse(userId);
    }

    public long countUnreadNotificationsByUserId(Long userId) {
        getUserById(userId);
        return notificationRepository.countByUser_IdAndLueFalse(userId);
    }

    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification non trouvee"));
    }

    public void deleteNotification(Long id) {
        Notification notif = getNotificationById(id);
        notificationRepository.delete(notif);
    }

    public void deleteAllNotifications() {
        notificationRepository.deleteAll();
    }

    public Notification markAsRead(Long id) {
        Notification notif = getNotificationById(id);
        notif.setLue(true);
        return notificationRepository.save(notif);
    }

    @Transactional
    public List<Notification> markAllAsRead(Long userId) {
        getUserById(userId);

        List<Notification> notifications = notificationRepository.findByUser_Id(userId);
        if (notifications.isEmpty()) {
            throw new IllegalArgumentException("Aucune notification trouvee pour cet utilisateur");
        }

        notifications.forEach(notification -> notification.setLue(true));
        return notificationRepository.saveAll(notifications);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouve"));
    }

    public List<User> getUsersByRoleId(Long roleId) {
        return userRepository.findDistinctByRoles_Id(roleId);
    }

    public Notification createNotifPush(User user, String message) {
        Notification notification = buildNotification(user, message, NotificationType.PUSH);
        return notificationRepository.save(notification);
    }

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

    @Transactional
    public void sendEmailToAllUsers(String message) {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new IllegalArgumentException("Aucun utilisateur trouve");
        }

        users.forEach(user -> createNotifEmail(user, message));
    }

    @Transactional
    public void sendEmailToOnlySubscriber(String message) {
        List<User> subscribers = getActiveSubscribers();
        if (subscribers.isEmpty()) {
            throw new IllegalArgumentException("Aucun abonne actif trouve");
        }

        subscribers.forEach(user -> createNotifEmail(user, message));
    }

    @Transactional
    public void pushNotifToAllUsers(String message) {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new IllegalArgumentException("Aucun utilisateur trouve");
        }

        users.forEach(user -> createNotifPush(user, message));
    }

    @Transactional
    public void pushNotifToOnlySubscriber(String message) {
        List<User> subscribers = getActiveSubscribers();
        if (subscribers.isEmpty()) {
            throw new IllegalArgumentException("Aucun abonne actif trouve");
        }

        subscribers.forEach(user -> createNotifPush(user, message));
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

    private List<User> getActiveSubscribers() {
        LocalDate today = LocalDate.now();
        return userRepository.findAll().stream()
                .filter(user -> user.getAbonnements() != null
                        && user.getAbonnements().stream()
                            .anyMatch(sub -> sub.getDateFin() != null
                                && !sub.getDateFin().isBefore(today)))
                .collect(Collectors.toList());
    }

    private Notification buildNotification(User user, String message, NotificationType type) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setType(type);
        notification.setDateEnvoi(LocalDateTime.now());
        notification.setLue(false);
        return notification;
    }
}
