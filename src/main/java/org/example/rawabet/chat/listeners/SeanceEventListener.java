package org.example.rawabet.chat.listeners;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.chat.services.interfaces.IChatSessionService;
import org.example.rawabet.events.SeanceCreatedEvent;
import org.example.rawabet.events.SeanceDeletedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeanceEventListener {

    private final IChatSessionService chatSessionService;

    @EventListener
    public void onSeanceCreated(SeanceCreatedEvent event) {
        String chatName = event.getFilmTitle() + " \u2014 " + event.getSeanceTime();
        chatSessionService.createChatSession(
                event.getSeanceId(),
                chatName,
                event.getDurationMinutes()
        );
    }

    @EventListener
    public void onSeanceDeleted(SeanceDeletedEvent event) {
        chatSessionService.getActiveSessionBySeanceId(event.getSeanceId())
                .ifPresent(session -> chatSessionService.closeSession(session.getId()));
    }
}