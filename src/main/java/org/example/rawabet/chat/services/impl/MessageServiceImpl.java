package org.example.rawabet.chat.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.chat.dto.ChatMessageRequestDTO;
import org.example.rawabet.chat.dto.ChatMessageResponseDTO;
import org.example.rawabet.chat.entities.ChatSession;
import org.example.rawabet.chat.entities.Message;
import org.example.rawabet.chat.repositories.ChatSessionRepository;
import org.example.rawabet.chat.repositories.MessageRepository;
import org.example.rawabet.chat.services.interfaces.IMessageService;
import org.example.rawabet.entities.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements IMessageService {

    private final MessageRepository messageRepository;
    private final ChatSessionRepository chatSessionRepository;

    @Override
    public ChatMessageResponseDTO sendMessage(ChatMessageRequestDTO request) {

        // 1. Vérification du contenu
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new RuntimeException("Le message ne peut pas être vide");
        }

        // 2. Vérification que la session existe et est encore active
        ChatSession session = chatSessionRepository.findById(request.getChatSessionId())
                .orElseThrow(() -> new RuntimeException("Session de chat introuvable"));

        // Auto-désactivation si le temps est écoulé
        if (session.isActive() && LocalDateTime.now().isAfter(session.getEndTime())) {
            session.setActive(false);
            chatSessionRepository.save(session);
        }

        if (!session.isActive()) {
            throw new RuntimeException("Ce chat est terminé, vous ne pouvez plus envoyer de messages");
        }

        // 3. Récupération de l'utilisateur connecté via le contexte de sécurité (JWT)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Vous devez être connecté pour envoyer un message");
        }

        User user = (User) authentication.getPrincipal();

        // 4. Création et sauvegarde du message
        Message message = Message.builder()
                .chatSessionId(request.getChatSessionId())
                .userId(user.getId())
                .content(request.getContent().trim())
                .build(); // createdAt géré par @PrePersist

        Message saved = messageRepository.save(message);
        return toDTO(saved);
    }

    @Override
    public List<ChatMessageResponseDTO> getMessages(Long chatSessionId) {
        return messageRepository
                .findByChatSessionIdOrderByCreatedAtAsc(chatSessionId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // Mapping manuel entité → DTO
    private ChatMessageResponseDTO toDTO(Message message) {
        return ChatMessageResponseDTO.builder()
                .id(message.getId())
                .chatSessionId(message.getChatSessionId())
                .userId(message.getUserId())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();
    }
}