package org.example.rawabet.chat.websocket;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.chat.dto.ChatMessageRequestDTO;
import org.example.rawabet.chat.dto.ChatMessageResponseDTO;
import org.example.rawabet.chat.dto.TypingEventDTO;
import org.example.rawabet.chat.services.interfaces.IMessageService;
import org.example.rawabet.entities.User;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final IMessageService messageService;

    @MessageMapping("/chat/{chatSessionId}/send")
    @SendTo("/topic/chat/{chatSessionId}")
    public ChatMessageResponseDTO send(
            @DestinationVariable Long chatSessionId,
            ChatMessageRequestDTO request,
            Principal principal) {

        if (principal == null) {
            throw new RuntimeException("Connectez-vous pour participer à la discussion");
        }

        SecurityContextHolder.getContext().setAuthentication((Authentication) principal);
        try {
            request.setChatSessionId(chatSessionId);
            return messageService.sendMessage(request);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @MessageMapping("/chat/{chatSessionId}/typing")
    @SendTo("/topic/chat/{chatSessionId}/typing")
    public TypingEventDTO typing(
            @DestinationVariable Long chatSessionId,
            Principal principal) {

        if (principal == null) {
            throw new RuntimeException("Connectez-vous pour participer à la discussion");
        }

        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) principal;
        User user = (User) auth.getPrincipal();
        return new TypingEventDTO(user.getNom());
    }
}
