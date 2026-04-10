package org.example.rawabet.chat.controllers;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.chat.dto.ChatSessionResponseDTO;
import org.example.rawabet.chat.services.interfaces.IChatSessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final IChatSessionService chatSessionService;

    // ── PUBLIC ──────────────────────────────────────────────────────────────

    @GetMapping("/join/{code}")
    public ResponseEntity<ChatSessionResponseDTO> joinChat(@PathVariable String code) {
        return ResponseEntity.ok(chatSessionService.getByCode(code));
    }

    @GetMapping("/active/{code}")
    public ResponseEntity<Boolean> isActive(@PathVariable String code) {
        return ResponseEntity.ok(chatSessionService.isChatActive(code));
    }

    @GetMapping("/session/seance/{seanceId}")
    public ResponseEntity<?> getActiveSessionBySeance(@PathVariable Long seanceId) {
        return chatSessionService.getActiveSessionBySeanceId(seanceId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    // ── ADMIN (CINEMA_CREATE) ────────────────────────────────────────────────

    /**
     * Recréer manuellement une session (relance, incident...).
     * La création automatique passe par SeanceEventListener.
     */
    @PostMapping("/session/{seanceId}/restart")
    public ResponseEntity<ChatSessionResponseDTO> restartChat(
            @PathVariable Long seanceId,
            @RequestParam String name,
            @RequestParam(defaultValue = "120") int durationMinutes) {
        return ResponseEntity.ok(
                chatSessionService.createChatSession(seanceId, name, durationMinutes));
    }

    @PutMapping("/session/{sessionId}/close")
    public ResponseEntity<ChatSessionResponseDTO> closeSession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(chatSessionService.closeSession(sessionId));
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<ChatSessionResponseDTO>> getAllSessions() {
        return ResponseEntity.ok(chatSessionService.getAllSessions());
    }
}