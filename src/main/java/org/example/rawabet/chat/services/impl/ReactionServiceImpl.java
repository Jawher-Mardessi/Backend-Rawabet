package org.example.rawabet.chat.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.chat.dto.ReactionEventDTO;
import org.example.rawabet.chat.dto.ReactionRequestDTO;
import org.example.rawabet.chat.entities.MessageReaction;
import org.example.rawabet.chat.repositories.MessageReactionRepository;
import org.example.rawabet.chat.services.interfaces.IReactionService;
import org.example.rawabet.entities.User;
import org.example.rawabet.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReactionServiceImpl implements IReactionService {

    private static final Set<String> ALLOWED_EMOJIS = Set.of("👍", "❤️", "😂", "😮", "😢");

    private final MessageReactionRepository reactionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ReactionEventDTO react(ReactionRequestDTO request, Long userId, Long chatSessionId) {

        if (!ALLOWED_EMOJIS.contains(request.getEmoji())) {
            throw new IllegalArgumentException("Emoji non autorisé : " + request.getEmoji());
        }

        Optional<MessageReaction> existing =
                reactionRepository.findByMessageIdAndUserId(request.getMessageId(), userId);

        if (existing.isPresent()) {
            MessageReaction reaction = existing.get();
            if (reaction.getEmoji().equals(request.getEmoji())) {
                // Même emoji → toggle off
                reactionRepository.delete(reaction);
                reactionRepository.flush();
            } else {
                // Emoji différent → mise à jour
                reaction.setEmoji(request.getEmoji());
                reactionRepository.save(reaction);
            }
        } else {
            MessageReaction reaction = MessageReaction.builder()
                    .messageId(request.getMessageId())
                    .userId(userId)
                    .emoji(request.getEmoji())
                    .chatSessionId(chatSessionId)
                    .build();
            reactionRepository.save(reaction);
        }

        return buildEvent(request.getMessageId());
    }

    @Override
    public Map<Long, Map<String, Long>> getReactionsForSession(Long chatSessionId) {
        List<MessageReaction> reactions = reactionRepository.findByChatSessionId(chatSessionId);
        return reactions.stream()
                .collect(Collectors.groupingBy(
                        MessageReaction::getMessageId,
                        Collectors.groupingBy(
                                MessageReaction::getEmoji,
                                Collectors.counting()
                        )
                ));
    }

    @Override
    public Map<Long, Map<String, List<String>>> getReactionUsersForSession(Long chatSessionId) {
        List<MessageReaction> reactions = reactionRepository.findByChatSessionId(chatSessionId);

        // Pré-charger tous les users impliqués en une seule requête
        Set<Long> userIds = reactions.stream()
                .map(MessageReaction::getUserId)
                .collect(Collectors.toSet());
        Map<Long, String> userNames = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getNom));

        return reactions.stream()
                .collect(Collectors.groupingBy(
                        MessageReaction::getMessageId,
                        Collectors.groupingBy(
                                MessageReaction::getEmoji,
                                Collectors.mapping(
                                        r -> userNames.getOrDefault(r.getUserId(), "?"),
                                        Collectors.toList()
                                )
                        )
                ));
    }

    // ── Helpers privés ────────────────────────────────────────────────────────

    private ReactionEventDTO buildEvent(Long messageId) {
        List<MessageReaction> all = reactionRepository.findByMessageId(messageId);

        // Pré-charger les noms
        Set<Long> userIds = all.stream().map(MessageReaction::getUserId).collect(Collectors.toSet());
        Map<Long, String> userNames = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getNom));

        Map<String, Long> counts = new LinkedHashMap<>();
        Map<String, List<String>> users = new LinkedHashMap<>();

        for (MessageReaction r : all) {
            String emoji = r.getEmoji();
            String name = userNames.getOrDefault(r.getUserId(), "?");
            counts.merge(emoji, 1L, Long::sum);
            users.computeIfAbsent(emoji, k -> new ArrayList<>()).add(name);
        }

        return ReactionEventDTO.builder()
                .messageId(messageId)
                .counts(counts)
                .users(users)
                .build();
    }
}