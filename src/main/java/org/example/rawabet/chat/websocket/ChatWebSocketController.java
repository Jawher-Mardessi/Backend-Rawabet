package org.example.rawabet.chat.websocket;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.chat.dto.*;
import org.example.rawabet.chat.entities.ChatSession;
import org.example.rawabet.chat.repositories.ChatSessionRepository;
import org.example.rawabet.chat.services.impl.RawaBotService;
import org.example.rawabet.chat.services.interfaces.IMessageService;
import org.example.rawabet.chat.services.interfaces.IReactionService;
import org.example.rawabet.entities.User;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final IMessageService messageService;
    private final IReactionService reactionService;
    private final RawaBotService rawaBotService;
    private final ChatSessionRepository chatSessionRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/{chatSessionId}/send")
    @SendTo("/topic/chat/{chatSessionId}")
    public ChatMessageResponseDTO send(
            @DestinationVariable Long chatSessionId,
            ChatMessageRequestDTO request,
            Principal principal) {

        if (principal == null) throw new RuntimeException("Connectez-vous pour participer");
        SecurityContextHolder.getContext().setAuthentication((Authentication) principal);
        try {
            request.setChatSessionId(chatSessionId);
            ChatMessageResponseDTO userMessage = messageService.sendMessage(request);

            // Détecter la mention @rawabot
            String content = request.getContent().trim();
            if (content.toLowerCase().startsWith("@rawabot")) {
                String question = content.substring(10).trim(); // retirer "@rawabot"
                if (!question.isEmpty()) {
                    // Appel asynchrone pour ne pas bloquer la réponse utilisateur
                    new Thread(() -> {
                        try {
                            ChatSession session = chatSessionRepository.findById(chatSessionId).orElse(null);
                            String filmName = session != null
                                    ? session.getName().split("—")[0].trim()
                                    : "ce film";

                            String botResponse = rawaBotService.ask(filmName, question);

                            ChatMessageResponseDTO botMessage = ChatMessageResponseDTO.builder()
                                    .id(-System.currentTimeMillis()) // ID unique négatif pour le bot
                                    .chatSessionId(chatSessionId)
                                    .userId(0L)
                                    .username("🎬 RawaBot")
                                    .content(botResponse)
                                    .createdAt(LocalDateTime.now())
                                    .deleted(false)
                                    .edited(false)
                                    .build();

                            // Broadcaster la réponse du bot à tous
                            messagingTemplate.convertAndSend(
                                    "/topic/chat/" + chatSessionId,
                                    botMessage
                            );
                        } catch (Exception e) {
                            System.err.println("[CinephileBot] Erreur thread : " + e.getMessage());
                        }
                    }).start();
                }
            }

            return userMessage;
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @MessageMapping("/chat/{chatSessionId}/typing")
    @SendTo("/topic/chat/{chatSessionId}/typing")
    public TypingEventDTO typing(
            @DestinationVariable Long chatSessionId,
            Principal principal) {

        if (principal == null) throw new RuntimeException("Connectez-vous pour participer");
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) principal;
        User user = (User) auth.getPrincipal();
        return new TypingEventDTO(user.getNom());
    }

    @MessageMapping("/chat/{chatSessionId}/react")
    @SendTo("/topic/chat/{chatSessionId}/reactions")
    public ReactionEventDTO react(
            @DestinationVariable Long chatSessionId,
            ReactionRequestDTO request,
            Principal principal) {

        if (principal == null) throw new RuntimeException("Connectez-vous pour réagir");
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) principal;
        User user = (User) auth.getPrincipal();
        return reactionService.react(request, user.getId(), chatSessionId);
    }

    @MessageMapping("/chat/{chatSessionId}/unsend")
    @SendTo("/topic/chat/{chatSessionId}/unsend")
    public UnsendEventDTO unsend(
            @DestinationVariable Long chatSessionId,
            UnsendRequestDTO request,
            Principal principal) {

        if (principal == null) throw new RuntimeException("Connectez-vous pour supprimer");
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) principal;
        User user = (User) auth.getPrincipal();
        return messageService.unsendMessage(request, user.getId());
    }

    @MessageMapping("/chat/{chatSessionId}/edit")
    @SendTo("/topic/chat/{chatSessionId}/edit")
    public ChatMessageResponseDTO edit(
            @DestinationVariable Long chatSessionId,
            EditRequestDTO request,
            Principal principal) {

        if (principal == null) throw new RuntimeException("Connectez-vous pour modifier");
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) principal;
        User user = (User) auth.getPrincipal();
        return messageService.editMessage(request, user.getId());
    }
}