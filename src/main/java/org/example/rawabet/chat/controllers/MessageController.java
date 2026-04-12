package org.example.rawabet.chat.controllers;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.chat.dto.ChatMessageResponseDTO;
import org.example.rawabet.chat.dto.MessagePageDTO;
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
     * Historique paginé — les plus récents d'abord (DESC), retournés en ASC pour l'affichage.
     * GET /chat/messages/{chatSessionId}?page=0&size=20
     *
     * page=0 → 20 derniers messages (chargement initial)
     * page=1 → les 20 précédents ("Charger plus")
     */
    @GetMapping("/{chatSessionId}")
    public ResponseEntity<MessagePageDTO> getMessages(
            @PathVariable Long chatSessionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        // Sécurité : limiter la taille max à 50 par requête
        size = Math.min(size, 50);
        return ResponseEntity.ok(messageService.getMessagesPaged(chatSessionId, page, size));
    }
}