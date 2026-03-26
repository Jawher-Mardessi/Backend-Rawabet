package org.example.rawabet.services;

import org.example.rawabet.entities.Notification;

import java.util.List;

public interface INotificationService {

    Notification addNotification(Notification notification);

    Notification updateNotification(Notification notification);

    void deleteNotification(Long id);

    Notification getById(Long id);

    List<Notification> getAll();
}