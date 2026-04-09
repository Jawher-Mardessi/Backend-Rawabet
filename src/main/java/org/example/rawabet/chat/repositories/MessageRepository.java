package org.example.rawabet.chat.repositories;

import org.example.rawabet.chat.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByChatSessionIdOrderByCreatedAtAsc(Long chatSessionId);
}