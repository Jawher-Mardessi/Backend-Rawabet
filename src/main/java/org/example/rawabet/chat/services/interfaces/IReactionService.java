package org.example.rawabet.chat.services.interfaces;

import org.example.rawabet.chat.dto.ReactionEventDTO;
import org.example.rawabet.chat.dto.ReactionRequestDTO;

import java.util.List;
import java.util.Map;

public interface IReactionService {

    ReactionEventDTO react(ReactionRequestDTO request, Long userId, Long chatSessionId);

    // counts seuls : Map<messageId, Map<emoji, count>>
    Map<Long, Map<String, Long>> getReactionsForSession(Long chatSessionId);

    // avec noms : Map<messageId, Map<emoji, List<username>>>
    Map<Long, Map<String, List<String>>> getReactionUsersForSession(Long chatSessionId);
}