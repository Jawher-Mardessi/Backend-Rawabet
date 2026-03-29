package org.example.rawabet.controllers;

import org.example.rawabet.entities.Notification;
import org.example.rawabet.entities.User;
import org.example.rawabet.services.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /** Lister toutes les notifications */
    @GetMapping("/all")
    public List<Notification> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    /** Envoyer une notification Push */
    @PostMapping("/push")
    public Notification sendPushNotification(
            @RequestParam Long userId,
            @RequestParam String message) {
        User user = notificationService.getUserById(userId);
        return notificationService.createNotifPush(user, message);
    }

    /** Envoyer une notifioded
     cation Email */
    @PostMapping("/email")
    public Notification sendEmailNotification(
            @RequestParam Long userId,
            @RequestParam String message) {
        User user = notificationService.getUserById(userId);
        return notificationService.createNotifEmail(user, message);
    }

    /** Supprimer une notification */
    @DeleteMapping("/{id}")
    public void deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
    }

    /** Marquer une notification comme lue */
    @PutMapping("/read/{id}")
    public Notification markNotificationAsRead(@PathVariable Long id) {
        return notificationService.markAsRead(id);
    }
}