package org.example.rawabet.chat.websocket;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.chat.dto.ChatMessageRequestDTO;
import org.example.rawabet.chat.dto.ChatMessageResponseDTO;
import org.example.rawabet.chat.services.interfaces.IMessageService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
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

        // WebSocketConfig n'utilise pas @EnableWebSocketSecurity :
        // SecurityContextHolder est vide dans les threads @MessageMapping.
        // On l'alimente manuellement depuis le principal STOMP (posé par
        // WebSocketAuthChannelInterceptor lors du CONNECT).
        SecurityContextHolder.getContext().setAuthentication((Authentication) principal);
        try {
            request.setChatSessionId(chatSessionId);
            return messageService.sendMessage(request);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
