package org.example.rawabet.chat.controllers;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.chat.dto.ChatMessageResponseDTO;
import org.example.rawabet.chat.services.interfaces.IMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat/messages")
@RequiredArgsConstructor
public class MessageController {

    private final IMessageService messageService;

    /**
     * Historique des messages d'une session (chargement initial).
     * L'envoi passe exclusivement par WebSocket (/app/chat/{id}/send).
     * GET /chat/messages/{chatSessionId}
     */
    @GetMapping("/{chatSessionId}")
    public ResponseEntity<List<ChatMessageResponseDTO>> getMessages(@PathVariable Long chatSessionId) {
        return ResponseEntity.ok(messageService.getMessages(chatSessionId));
    }
}