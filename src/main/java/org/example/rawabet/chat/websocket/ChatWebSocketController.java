package org.example.rawabet.chat.websocket;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.chat.dto.ChatMessageRequestDTO;
import org.example.rawabet.chat.dto.ChatMessageResponseDTO;
import org.example.rawabet.chat.services.interfaces.IMessageService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final IMessageService messageService;

    // Envoi   : /app/chat/{chatSessionId}/send
    // Écoute  : /topic/chat/{chatSessionId}
    //
    // Chaque séance a son propre canal → les messages n'arrivent qu'aux
    // participants du bon chat.
    @MessageMapping("/chat/{chatSessionId}/send")
    @SendTo("/topic/chat/{chatSessionId}")
    public ChatMessageResponseDTO send(
            @DestinationVariable Long chatSessionId,
            ChatMessageRequestDTO request,
            Principal principal) {

        // Vérification que l'utilisateur est bien authentifié
        if (principal == null) {
            throw new RuntimeException("Vous devez être connecté pour envoyer un message");
        }

        // On force le chatSessionId depuis l'URL (plus fiable que le body)
        request.setChatSessionId(chatSessionId);

        return messageService.sendMessage(request);
    }
}