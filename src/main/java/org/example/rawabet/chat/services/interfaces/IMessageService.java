package org.example.rawabet.chat.services.interfaces;

import org.example.rawabet.chat.dto.*;

import java.util.List;

public interface IMessageService {

    ChatMessageResponseDTO sendMessage(ChatMessageRequestDTO request);

    List<ChatMessageResponseDTO> getMessages(Long chatSessionId);

    MessagePageDTO getMessagesPaged(Long chatSessionId, int page, int size);

    // Unsend : pour moi ou pour tous
    UnsendEventDTO unsendMessage(UnsendRequestDTO request, Long userId);

    // Edit : modifie le contenu, broadcast le message mis à jour
    ChatMessageResponseDTO editMessage(EditRequestDTO request, Long userId);
}