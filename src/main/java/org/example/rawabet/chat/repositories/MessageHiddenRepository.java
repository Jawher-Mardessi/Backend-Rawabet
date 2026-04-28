package org.example.rawabet.chat.repositories;

import org.example.rawabet.chat.entities.MessageHidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface MessageHiddenRepository extends JpaRepository<MessageHidden, Long> {

    boolean existsByMessageIdAndUserId(Long messageId, Long userId);

    // Tous les IDs cachés par un user (JPQL explicite pour retourner les Long)
    @Query("SELECT h.messageId FROM MessageHidden h WHERE h.userId = :userId")
    List<Long> findHiddenMessageIdsByUserId(@Param("userId") Long userId);
}