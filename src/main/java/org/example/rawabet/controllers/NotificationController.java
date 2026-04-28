package org.example.rawabet.controllers;

import org.example.rawabet.entities.Notification;
import org.example.rawabet.entities.User;
import org.example.rawabet.services.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/all")
    public List<Map<String, Object>> getAllNotifications() {
        return notificationService.getAllNotifications().stream()
                .map(this::toNotificationResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/user/{userId}")
    public List<Map<String, Object>> getNotificationsByUser(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getNotificationsByUserId(userId);
        return notifications.stream()
                .map(this::toNotificationResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/unread/user/{userId}")
    public Map<String, Object> getUnreadNotificationsByUser(@PathVariable Long userId) {
        long count = notificationService.countUnreadNotificationsByUserId(userId);

        return Map.of(
                "success", true,
                "count", count
        );
    }

    private Map<String, Object> toNotificationResponse(Notification notification) {
        return Map.of(
                "id", notification.getId(),
                "message", notification.getMessage(),
                "dateEnvoi", notification.getDateEnvoi(),
                "type", notification.getType(),
                "lue", notification.isLue()
        );
    }

    @PostMapping("/push")
    public Map<String, Object> sendPushNotification(@RequestParam Long userId,
                                                    @RequestParam String message) {

        User user = notificationService.getUserById(userId);
        notificationService.createNotifPush(user, message);

        return Map.of(
                "success", true
        );
    }
    @PostMapping("/email")
    public Map<String, Object> sendEmailNotification(@RequestParam Long userId,
                                                     @RequestParam String message) {

        User user = notificationService.getUserById(userId);
        notificationService.createNotifEmail(user, message);

        return Map.of(
                "success", true,
                "message", "Email envoyé avec succès"
        );
    }

    @PostMapping("/sendemailtoallusers")
    public Map<String, Object> sendEmailToAllUsers(@RequestParam String message) {
        notificationService.sendEmailToAllUsers(message);
        return Map.of("success", true, "message", "success send");
    }

    @PostMapping("/sendemailtoonlysubscriber")
    public Map<String, Object> sendEmailToOnlySubscriber(@RequestParam String message) {
        notificationService.sendEmailToOnlySubscriber(message);
        return Map.of("success", true, "message", "success send");
    }

    @PostMapping("/pushnotiftoallusers")
    public Map<String, Object> pushNotifToAllUsers(@RequestParam String message) {
        notificationService.pushNotifToAllUsers(message);
        return Map.of("success", true, "message", "success send");
    }

    @PostMapping("/pushnotiftoonlysubscriber")
    public Map<String, Object> pushNotifToOnlySubscriber(@RequestParam String message) {
        notificationService.pushNotifToOnlySubscriber(message);
        return Map.of("success", true, "message", "success send");
    }

    @GetMapping("/users/role5")
    public List<Map<String, Object>> getUsersWithRoleFive() {
        return notificationService.getUsersByRoleId(5L).stream()
                .map(user -> Map.<String, Object>of(
                        "id", user.getId(),
                        "nom", user.getNom(),
                        "email", user.getEmail()
                ))
                .collect(Collectors.toList());
    }

    @GetMapping("/users/role/{roleId}")
    public List<Map<String, Object>> getUsersByRole(@PathVariable Long roleId) {
        return notificationService.getUsersByRoleId(roleId).stream()
                .map(user -> Map.<String, Object>of(
                        "id", user.getId(),
                        "nom", user.getNom(),
                        "email", user.getEmail()
                ))
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public void deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
    }

    @DeleteMapping("/delete/all")
    public Map<String, Object> deleteAllNotifications() {
        notificationService.deleteAllNotifications();
        return Map.of(
                "success", true
        );
    }

    @PutMapping("/read/{id}")
    public Map<String, Object> markNotificationAsRead(@PathVariable Long id) {
        Notification notification = notificationService.markAsRead(id);

        return Map.of(
                "success", true,
                "message", "Notification marquee comme lue",
                "id", notification.getId(),
                "lue", notification.isLue()
        );
    }

    @PutMapping("/read/user/{userId}")
    public Map<String, Object> markAllNotificationsAsRead(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.markAllAsRead(userId);

        return Map.of(
                "success", true,
                "message", "Notifications marquees comme lues",
                "userId", userId,
                "count", notifications.size()
        );
    }
}
