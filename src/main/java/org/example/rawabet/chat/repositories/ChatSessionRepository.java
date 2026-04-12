package org.example.rawabet.chat.repositories;

import org.example.rawabet.chat.entities.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    Optional<ChatSession> findByCode(String code);

    boolean existsByCode(String code);

    // Query dérivée Spring Data — plus sûre que JPQL manuel
    // Correspond au champ "active" (boolean) dans ChatSession
    Optional<ChatSession> findBySeanceIdAndActiveTrue(Long seanceId);
}