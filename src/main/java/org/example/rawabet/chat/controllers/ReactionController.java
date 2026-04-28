package org.example.rawabet.chat.controllers;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.chat.dto.ReactionEventDTO;
import org.example.rawabet.chat.services.interfaces.IReactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat/reactions")
@RequiredArgsConstructor
public class ReactionController {

    private final IReactionService reactionService;

    /**
     * Charge counts + users pour toutes les réactions d'une session.
     * GET /chat/reactions/{chatSessionId}
     * Retourne : Map<messageId, { counts: Map<emoji,count>, users: Map<emoji,[username]> }>
     */
    @GetMapping("/{chatSessionId}")
    public ResponseEntity<Map<Long, Map<String, Object>>> getReactionsForSession(
            @PathVariable Long chatSessionId) {

        Map<Long, Map<String, Long>> counts =
                reactionService.getReactionsForSession(chatSessionId);
        Map<Long, Map<String, List<String>>> users =
                reactionService.getReactionUsersForSession(chatSessionId);

        // Fusionner les deux maps en une seule réponse par messageId
        Map<Long, Map<String, Object>> result = new java.util.LinkedHashMap<>();
        counts.forEach((msgId, emojiCounts) -> {
            Map<String, Object> entry = new java.util.LinkedHashMap<>();
            entry.put("counts", emojiCounts);
            entry.put("users", users.getOrDefault(msgId, Map.of()));
            result.put(msgId, entry);
        });

        return ResponseEntity.ok(result);
    }
}