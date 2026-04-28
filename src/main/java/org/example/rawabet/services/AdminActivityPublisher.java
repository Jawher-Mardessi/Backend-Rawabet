package org.example.rawabet.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.rawabet.dto.AdminActivityEvent;
import org.example.rawabet.entities.ActivityLog;
import org.example.rawabet.repositories.ActivityLogRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminActivityPublisher {

    private final SimpMessagingTemplate  messagingTemplate;
    private final ActivityLogRepository  activityLogRepo;

    private static final String TOPIC = "/topic/admin/activity";

    /**
     * Publie un événement :
     * 1. Persiste en base de données (activity_log)
     * 2. Diffuse en temps réel via WebSocket
     */
    @Async
    public void publish(AdminActivityEvent event) {
        // ── 1. Persistance en BDD ─────────────────────────────────────
        try {
            ActivityLog log = ActivityLog.builder()
                    .type(event.getType())
                    .message(event.getMessage())
                    .detail(event.getDetail())
                    .icon(event.getIcon())
                    .color(event.getColor())
                    .build();
            activityLogRepo.save(log);
        } catch (Exception e) {
            log.warn("[AdminActivity] Failed to persist event: {}", e.getMessage());
        }

        // ── 2. Diffusion WebSocket ────────────────────────────────────
        try {
            messagingTemplate.convertAndSend(TOPIC, event);
            log.debug("[AdminActivity] Published: {} — {}", event.getType(), event.getDetail());
        } catch (Exception e) {
            log.warn("[AdminActivity] Failed to publish event: {}", e.getMessage());
        }
    }
}