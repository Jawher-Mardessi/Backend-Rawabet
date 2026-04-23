package org.example.rawabet.chat.repositories;

import org.example.rawabet.chat.entities.MessageReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageReactionRepository extends JpaRepository<MessageReaction, Long> {

    Optional<MessageReaction> findByMessageIdAndUserId(Long messageId, Long userId);

    // Toutes les réactions d'un message (pour buildEvent)
    List<MessageReaction> findByMessageId(Long messageId);

    // Toutes les réactions d'une session (chargement initial)
    List<MessageReaction> findByChatSessionId(Long chatSessionId);

    @Query("SELECT r.emoji, COUNT(r) FROM MessageReaction r WHERE r.messageId = :messageId GROUP BY r.emoji")
    List<Object[]> countByEmojiForMessage(@Param("messageId") Long messageId);
}