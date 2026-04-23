package org.example.rawabet.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.rawabet.dto.AdminActivityEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminActivityPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    private static final String TOPIC = "/topic/admin/activity";

    public void publish(AdminActivityEvent event) {
        try {
            messagingTemplate.convertAndSend(TOPIC, event);
            log.debug("[AdminActivity] Published: {} — {}", event.getType(), event.getDetail());
        } catch (Exception e) {
            log.warn("[AdminActivity] Failed to publish event: {}", e.getMessage());
        }
    }
}