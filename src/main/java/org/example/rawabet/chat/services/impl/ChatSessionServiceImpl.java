package org.example.rawabet.chat.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.chat.dto.ChatSessionResponseDTO;
import org.example.rawabet.chat.entities.ChatSession;
import org.example.rawabet.chat.repositories.ChatSessionRepository;
import org.example.rawabet.chat.services.interfaces.IChatSessionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ChatSessionServiceImpl implements IChatSessionService {

    private final ChatSessionRepository chatSessionRepository;

    @Override
    public ChatSessionResponseDTO createChatSession(Long seanceId) {

        // Génération d'un code unique à 4 chiffres
        String code;
        do {
            code = generateCode();
        } while (chatSessionRepository.existsByCode(code));

        LocalDateTime now = LocalDateTime.now();

        ChatSession chat = ChatSession.builder()
                .seanceId(seanceId)
                .code(code)
                .isActive(true)
                .startTime(now)
                .endTime(now.plusMinutes(12))
                .build();

        ChatSession saved = chatSessionRepository.save(chat);
        return toDTO(saved);
    }

    @Override
    public ChatSessionResponseDTO getByCode(String code) {
        ChatSession chat = chatSessionRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Chat introuvable avec le code : " + code));
        return toDTO(chat);
    }

    @Override
    public boolean isChatActive(String code) {
        ChatSession chat = chatSessionRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Chat introuvable avec le code : " + code));

        // Si le temps est écoulé et que le chat est encore marqué actif → on le désactive
        if (chat.isActive() && LocalDateTime.now().isAfter(chat.getEndTime())) {
            chat.setActive(false);
            chatSessionRepository.save(chat);
            return false;
        }

        return chat.isActive();
    }

    // Mapping manuel entité → DTO
    private ChatSessionResponseDTO toDTO(ChatSession chat) {
        return ChatSessionResponseDTO.builder()
                .id(chat.getId())
                .seanceId(chat.getSeanceId())
                .code(chat.getCode())
                .isActive(chat.isActive())
                .startTime(chat.getStartTime())
                .endTime(chat.getEndTime())
                .createdAt(chat.getCreatedAt())
                .build();
    }

    private String generateCode() {
        int number = new Random().nextInt(9000) + 1000;
        return String.valueOf(number);
    }
}