package org.example.rawabet.chat.repositories;

import org.example.rawabet.chat.entities.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    Optional<ChatSession> findByCode(String code);

    boolean existsByCode(String code);
}