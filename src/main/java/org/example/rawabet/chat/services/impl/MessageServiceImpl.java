package org.example.rawabet.chat.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.chat.dto.*;
import org.example.rawabet.chat.entities.Message;
import org.example.rawabet.chat.entities.MessageHidden;
import org.example.rawabet.chat.entities.ChatSession;
import org.example.rawabet.chat.repositories.ChatSessionRepository;
import org.example.rawabet.chat.repositories.MessageHiddenRepository;
import org.example.rawabet.chat.repositories.MessageRepository;
import org.example.rawabet.chat.services.interfaces.IMessageService;
import org.example.rawabet.entities.User;
import org.example.rawabet.repositories.UserRepository;
import org.example.rawabet.services.IAuthService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements IMessageService {

    private final MessageRepository messageRepository;
    private final MessageHiddenRepository messageHiddenRepository;
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
            throw new RuntimeException("Ce chat est terminé");
        }

        User user = authService.getAuthenticatedUser();

        Message message = Message.builder()
                .chatSessionId(request.getChatSessionId())
                .userId(user.getId())
                .content(request.getContent().trim())
                .build();

        return toDTO(messageRepository.save(message), user);
    }

    @Override
    public List<ChatMessageResponseDTO> getMessages(Long chatSessionId) {
        return messageRepository
                .findByChatSessionIdOrderByCreatedAtAsc(chatSessionId)
                .stream()
                .map(m -> toDTO(m, userRepository.findById(m.getUserId()).orElse(null)))
                .toList();
    }

    @Override
    public MessagePageDTO getMessagesPaged(Long chatSessionId, int page, int size) {
        Long currentUserId = null;
        try {
            currentUserId = authService.getAuthenticatedUser().getId();
        } catch (Exception ignored) {}

        Set<Long> hiddenIds = currentUserId != null
                ? new java.util.HashSet<>(messageHiddenRepository.findHiddenMessageIdsByUserId(currentUserId))
                : Set.of();

        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messagePage = messageRepository
                .findByChatSessionIdOrderByCreatedAtDesc(chatSessionId, pageable);

        Set<Long> userIds = messagePage.getContent().stream()
                .map(Message::getUserId).collect(Collectors.toSet());
        Map<Long, User> usersById = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        List<ChatMessageResponseDTO> messages = messagePage.getContent().stream()
                .filter(m -> !hiddenIds.contains(m.getId()))
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .map(m -> toDTO(m, usersById.get(m.getUserId())))
                .toList();

        return MessagePageDTO.builder()
                .messages(messages)
                .page(messagePage.getNumber())
                .size(messagePage.getSize())
                .totalElements(messagePage.getTotalElements())
                .totalPages(messagePage.getTotalPages())
                .hasMore(messagePage.hasNext())
                .build();
    }

    @Override
    @Transactional
    public UnsendEventDTO unsendMessage(UnsendRequestDTO request, Long userId) {
        Message message = messageRepository.findById(request.getMessageId())
                .orElseThrow(() -> new RuntimeException("Message introuvable"));

        if (!message.getUserId().equals(userId)) {
            throw new RuntimeException("Vous ne pouvez supprimer que vos propres messages");
        }

        if (request.isForEveryone()) {
            message.setDeleted(true);
            messageRepository.save(message);
        } else {
            if (!messageHiddenRepository.existsByMessageIdAndUserId(request.getMessageId(), userId)) {
                messageHiddenRepository.save(MessageHidden.builder()
                        .messageId(request.getMessageId())
                        .userId(userId)
                        .build());
            }
        }

        return UnsendEventDTO.builder()
                .messageId(request.getMessageId())
                .forEveryone(request.isForEveryone())
                .build();
    }

    @Override
    @Transactional
    public ChatMessageResponseDTO editMessage(EditRequestDTO request, Long userId) {
        Message message = messageRepository.findById(request.getMessageId())
                .orElseThrow(() -> new RuntimeException("Message introuvable"));

        if (!message.getUserId().equals(userId)) {
            throw new RuntimeException("Vous ne pouvez modifier que vos propres messages");
        }

        if (message.isDeleted()) {
            throw new RuntimeException("Impossible de modifier un message supprimé");
        }

        String newContent = request.getNewContent();
        if (newContent == null || newContent.trim().isEmpty()) {
            throw new RuntimeException("Le contenu ne peut pas être vide");
        }

        message.setContent(newContent.trim());
        message.setEdited(true);
        message.setEditedAt(LocalDateTime.now());
        messageRepository.save(message);

        User user = userRepository.findById(userId).orElse(null);
        return toDTO(message, user);
    }

    @Override
    @Transactional
    public void adminDeleteMessage(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message introuvable : " + messageId));
        message.setDeleted(true);
        messageRepository.save(message);
    }

    private ChatMessageResponseDTO toDTO(Message message, User user) {
        // ✅ BUG 2 CORRIGÉ : userId 0 = RawaBot, pas besoin de chercher en base
        boolean isBot = message.getUserId() != null && message.getUserId() == 0L;

        return ChatMessageResponseDTO.builder()
                .id(message.getId())
                .chatSessionId(message.getChatSessionId())
                .userId(message.getUserId())
                .username(isBot ? "🎬 RawaBot" : (user != null ? user.getNom() : "Utilisateur inconnu"))
                .userEmail(isBot ? null : (user != null ? user.getEmail() : null))
                .content(message.isDeleted() ? "" : message.getContent())
                .createdAt(message.getCreatedAt())
                .deleted(message.isDeleted())
                .edited(message.isEdited())
                .editedAt(message.getEditedAt())
                .spoiler(message.isSpoiler())
                .build();
    }
}