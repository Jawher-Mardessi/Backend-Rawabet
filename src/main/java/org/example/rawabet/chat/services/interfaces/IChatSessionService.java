package org.example.rawabet.chat.services.interfaces;

import org.example.rawabet.chat.dto.ChatSessionResponseDTO;

public interface IChatSessionService {

    ChatSessionResponseDTO createChatSession(Long seanceId);

    ChatSessionResponseDTO getByCode(String code);

    boolean isChatActive(String code);
}