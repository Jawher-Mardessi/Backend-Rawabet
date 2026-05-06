package org.example.rawabet.repositories;

import org.example.rawabet.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUser_Id(Long userId);

    List<Notification> findByUser_IdAndLueFalse(Long userId);

    long countByUser_IdAndLueFalse(Long userId);
}
