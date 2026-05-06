package org.example.rawabet.repositories;

import org.example.rawabet.entities.FidelityHistory;
import org.example.rawabet.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FidelityHistoryRepository extends JpaRepository<FidelityHistory, Long> {

    List<FidelityHistory> findByUser(User user);

    // Nécessaire pour getMyHistory(Pageable) dans ICarteFideliteService
    Page<FidelityHistory> findByUser(User user, Pageable pageable);
}