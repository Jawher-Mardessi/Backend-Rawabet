package org.example.rawabet.chat.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.chat.dto.ChatSessionResponseDTO;
import org.example.rawabet.chat.entities.ChatSession;
import org.example.rawabet.chat.repositories.ChatSessionRepository;
import org.example.rawabet.chat.services.interfaces.IChatSessionService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatSessionServiceImpl implements IChatSessionService {

    private final ChatSessionRepository chatSessionRepository;

    private static final int FALLBACK_DURATION_MINUTES = 120;

    // ✅ BUG 8 CORRIGÉ : SecureRandom au lieu de Random (non prévisible)
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Override
    public ChatSessionResponseDTO createChatSession(Long seanceId, String name, int durationMinutes) {

        chatSessionRepository.findBySeanceIdAndActiveTrue(seanceId).ifPresent(existing -> {
            existing.setActive(false);
            chatSessionRepository.save(existing);
        });

        String code;
        do {
            code = generateCode();
        } while (chatSessionRepository.existsByCode(code));

        LocalDateTime now = LocalDateTime.now();
        int duration = durationMinutes > 0 ? durationMinutes : FALLBACK_DURATION_MINUTES;

        ChatSession chat = ChatSession.builder()
                .seanceId(seanceId)
                .name(name)
                .code(code)
                .active(true)
                .startTime(now)
                .endTime(now.plusMinutes(duration))
                .build();

        return toDTO(chatSessionRepository.save(chat));
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

        if (chat.isActive() && LocalDateTime.now().isAfter(chat.getEndTime())) {
            chat.setActive(false);
            chatSessionRepository.save(chat);
            return false;
        }

        return chat.isActive();
    }

    @Override
    public ChatSessionResponseDTO closeSession(Long sessionId) {
        ChatSession chat = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session introuvable : " + sessionId));
        chat.setActive(false);
        chat.setEndTime(LocalDateTime.now());
        return toDTO(chatSessionRepository.save(chat));
    }

    @Override
    public Optional<ChatSessionResponseDTO> getActiveSessionBySeanceId(Long seanceId) {
        return chatSessionRepository.findBySeanceIdAndActiveTrue(seanceId).map(this::toDTO);
    }

    @Override
    public List<ChatSessionResponseDTO> getAllSessions() {
        return chatSessionRepository.findAll().stream().map(this::toDTO).toList();
    }

    private ChatSessionResponseDTO toDTO(ChatSession chat) {
        return ChatSessionResponseDTO.builder()
                .id(chat.getId())
                .seanceId(chat.getSeanceId())
                .name(chat.getName())
                .code(chat.getCode())
                .active(chat.isActive())
                .startTime(chat.getStartTime())
                .endTime(chat.getEndTime())
                .createdAt(chat.getCreatedAt())
                .build();
    }

    private String generateCode() {
        int number = SECURE_RANDOM.nextInt(9000) + 1000;
        return String.valueOf(number);
    }
}
