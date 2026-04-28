package org.example.rawabet.chat.repositories;

import org.example.rawabet.chat.entities.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // Historique complet (utilisé par le WebSocket pour toDTO après envoi)
    List<Message> findByChatSessionIdOrderByCreatedAtAsc(Long chatSessionId);

    // Paginé — trié DESC pour récupérer les plus récents en premier
    Page<Message> findByChatSessionIdOrderByCreatedAtDesc(Long chatSessionId, Pageable pageable);
}