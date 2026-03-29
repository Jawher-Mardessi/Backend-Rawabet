package org.example.rawabet.repositories;

import org.example.rawabet.entities.ChatInstantane;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatInstantaneRepository extends JpaRepository<ChatInstantane, Long> {

    List<ChatInstantane> findBySeanceId(Long seanceId);

    List<ChatInstantane> findByUserId(Long userId);

}