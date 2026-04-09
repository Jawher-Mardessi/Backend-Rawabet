package org.example.rawabet.chat.services.interfaces;

import org.example.rawabet.chat.dto.ChatMessageRequestDTO;
import org.example.rawabet.chat.dto.ChatMessageResponseDTO;

import java.util.List;

public interface IMessageService {

    ChatMessageResponseDTO sendMessage(ChatMessageRequestDTO request);

    List<ChatMessageResponseDTO> getMessages(Long chatSessionId);
}