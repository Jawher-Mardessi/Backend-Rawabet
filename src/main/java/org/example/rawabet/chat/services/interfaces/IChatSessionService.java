package org.example.rawabet.chat.services.interfaces;

import org.example.rawabet.chat.dto.ChatSessionResponseDTO;

import java.util.List;
import java.util.Optional;

public interface IChatSessionService {

    /**
     * Crée une session de chat liée à une séance.
     * Appelé via l'événement SeanceCreatedEvent (module chat indépendant).
     *
     * @param seanceId        id de la séance
     * @param name            ex: "Inception — 20:30"
     * @param durationMinutes durée du film, 0 → fallback 120 min
     */
    ChatSessionResponseDTO createChatSession(Long seanceId, String name, int durationMinutes);

    ChatSessionResponseDTO getByCode(String code);

    boolean isChatActive(String code);

    ChatSessionResponseDTO closeSession(Long sessionId);

    Optional<ChatSessionResponseDTO> getActiveSessionBySeanceId(Long seanceId);

    List<ChatSessionResponseDTO> getAllSessions();
}