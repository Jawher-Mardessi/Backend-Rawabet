package org.example.rawabet.chat.controllers;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.chat.dto.ChatMessageRequestDTO;
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

    // 💬 Envoyer un message — nécessite d'être authentifié (JWT)
    // POST /chat/messages
    @PostMapping
    public ResponseEntity<ChatMessageResponseDTO> sendMessage(@RequestBody ChatMessageRequestDTO request) {
        return ResponseEntity.ok(messageService.sendMessage(request));
    }

    // 📥 Récupérer tous les messages d'une session (public — lecture sans auth)
    // GET /chat/messages/{chatSessionId}
    @GetMapping("/{chatSessionId}")
    public ResponseEntity<List<ChatMessageResponseDTO>> getMessages(@PathVariable Long chatSessionId) {
        return ResponseEntity.ok(messageService.getMessages(chatSessionId));
    }
}