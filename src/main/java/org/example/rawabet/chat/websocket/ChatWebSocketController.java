package org.example.rawabet.chat.websocket;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.chat.dto.*;
import org.example.rawabet.chat.entities.ChatSession;
import org.example.rawabet.chat.entities.Message;
import org.example.rawabet.chat.repositories.ChatSessionRepository;
import org.example.rawabet.chat.repositories.MessageRepository;
import org.example.rawabet.chat.services.impl.RawaBotService;
import org.example.rawabet.chat.services.impl.SpoilerDetectionServiceImpl;
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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final IMessageService messageService;
    private final IReactionService reactionService;
    private final RawaBotService rawaBotService;
    private final SpoilerDetectionServiceImpl spoilerDetectionService;
    private final ChatSessionRepository chatSessionRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // ✅ ID réservé pour RawaBot en base
    private static final Long BOT_USER_ID = 0L;
    private static final String BOT_USERNAME = "🎬 RawaBot";

    // ✅ Regex : détecte @rawabot n'importe où dans le message (insensible à la casse)
    private static final Pattern BOT_MENTION = Pattern.compile(
            "@rawabot\\s+(.+)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

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
            String content = request.getContent().trim();
            Long messageId = userMessage.getId();

            // ✅ Détection spoiler ASYNCHRONE — ne bloque pas le chat
            new Thread(() -> {
                try {
                    boolean spoiler = spoilerDetectionService.isSpoiler(content, messageId);
                    if (spoiler) {
                        messagingTemplate.convertAndSend(
                                "/topic/chat/" + chatSessionId + "/spoiler",
                                Map.of("messageId", messageId, "isSpoiler", true)
                        );
                    }
                } catch (Exception e) {
                    System.err.println("[Spoiler] Erreur : " + e.getMessage());
                }
            }).start();

            // ✅ BUG 1 CORRIGÉ : détecte @rawabot n'importe où dans le message
            Matcher matcher = BOT_MENTION.matcher(content);
            if (matcher.find()) {
                String question = matcher.group(1).trim();
                if (!question.isEmpty()) {
                    new Thread(() -> {
                        try {
                            ChatSession session = chatSessionRepository.findById(chatSessionId).orElse(null);
                            String filmName = session != null
                                    ? session.getName().split("—")[0].trim()
                                    : "ce film";

                            String botResponse = rawaBotService.ask(filmName, question);

                            // ✅ BUG 1 CORRIGÉ : on sauvegarde le message bot en base
                            Message botEntity = Message.builder()
                                    .chatSessionId(chatSessionId)
                                    .userId(BOT_USER_ID)
                                    .content(botResponse)
                                    .deleted(false)
                                    .edited(false)
                                    .spoiler(false)
                                    .build();
                            Message savedBot = messageRepository.save(botEntity);

                            ChatMessageResponseDTO botMessage = ChatMessageResponseDTO.builder()
                                    .id(savedBot.getId())
                                    .chatSessionId(chatSessionId)
                                    .userId(BOT_USER_ID)
                                    .username(BOT_USERNAME)
                                    .content(botResponse)
                                    .createdAt(savedBot.getCreatedAt())
                                    .deleted(false)
                                    .edited(false)
                                    .spoiler(false)
                                    .build();

                            messagingTemplate.convertAndSend(
                                    "/topic/chat/" + chatSessionId,
                                    botMessage
                            );
                        } catch (Exception e) {
                            System.err.println("[RawaBot] Erreur thread : " + e.getMessage());
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