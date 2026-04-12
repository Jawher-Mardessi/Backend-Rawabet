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
import org.example.rawabet.repositories.UserRepository;
import org.example.rawabet.services.IAuthService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements IMessageService {

    private final MessageRepository messageRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final IAuthService authService;
    private final UserRepository userRepository;

    @Override
    public ChatMessageResponseDTO sendMessage(ChatMessageRequestDTO request) {

        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new RuntimeException("Le message ne peut pas être vide");
        }

        ChatSession session = chatSessionRepository.findById(request.getChatSessionId())
                .orElseThrow(() -> new RuntimeException("Session de chat introuvable"));

        if (session.isActive() && LocalDateTime.now().isAfter(session.getEndTime())) {
            session.setActive(false);
            chatSessionRepository.save(session);
        }

        if (!session.isActive()) {
            throw new RuntimeException("Ce chat est terminé, vous ne pouvez plus envoyer de messages");
        }

        User user = authService.getAuthenticatedUser();

        Message message = Message.builder()
                .chatSessionId(request.getChatSessionId())
                .userId(user.getId())
                .content(request.getContent().trim())
                .build();

        Message saved = messageRepository.save(message);
        return toDTO(saved, user);
    }

    @Override
    public List<ChatMessageResponseDTO> getMessages(Long chatSessionId) {
        return messageRepository
                .findByChatSessionIdOrderByCreatedAtAsc(chatSessionId)
                .stream()
                .map(message -> {
                    User user = userRepository.findById(message.getUserId()).orElse(null);
                    return toDTO(message, user);
                })
                .toList();
    }

    private ChatMessageResponseDTO toDTO(Message message, User user) {
        return ChatMessageResponseDTO.builder()
                .id(message.getId())
                .chatSessionId(message.getChatSessionId())
                .userId(message.getUserId())
                .username(user != null ? user.getNom() : "Utilisateur inconnu")
                .userEmail(user != null ? user.getEmail() : null)
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();
    }
}