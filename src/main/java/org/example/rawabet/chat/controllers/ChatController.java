package org.example.rawabet.chat.controllers;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.chat.dto.ChatSessionResponseDTO;
import org.example.rawabet.chat.services.interfaces.IChatSessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final IChatSessionService chatSessionService;

    // 🎬 Créer un chat pour une séance (appelé quand la séance démarre)
    @PostMapping("/session/{seanceId}")
    public ResponseEntity<ChatSessionResponseDTO> createChat(@PathVariable Long seanceId) {
        return ResponseEntity.ok(chatSessionService.createChatSession(seanceId));
    }

    // 🔑 Rejoindre le chat via le code à 4 chiffres (public)
    @GetMapping("/join/{code}")
    public ResponseEntity<ChatSessionResponseDTO> joinChat(@PathVariable String code) {
        return ResponseEntity.ok(chatSessionService.getByCode(code));
    }

    // ✅ Vérifier si le chat est encore actif (public)
    @GetMapping("/active/{code}")
    public ResponseEntity<Boolean> isActive(@PathVariable String code) {
        return ResponseEntity.ok(chatSessionService.isChatActive(code));
    }
}