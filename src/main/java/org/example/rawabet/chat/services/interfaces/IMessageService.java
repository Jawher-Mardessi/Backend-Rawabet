package org.example.rawabet.chat.services.interfaces;

import org.example.rawabet.chat.dto.ChatMessageRequestDTO;
import org.example.rawabet.chat.dto.ChatMessageResponseDTO;

import java.util.List;

public interface IMessageService {

    /**
     * Envoie un message dans une session de chat active.
     * Appelé uniquement depuis le ChatWebSocketController.
     */
    ChatMessageResponseDTO sendMessage(ChatMessageRequestDTO request);

    /**
     * Récupère l'historique des messages d'une session, triés chronologiquement.
     */
    List<ChatMessageResponseDTO> getMessages(Long chatSessionId);
}