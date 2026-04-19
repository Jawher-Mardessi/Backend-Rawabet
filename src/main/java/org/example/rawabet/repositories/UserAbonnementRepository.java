package org.example.rawabet.repositories;

import org.example.rawabet.entities.UserAbonnement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserAbonnementRepository extends JpaRepository<UserAbonnement, Long> {
    List<UserAbonnement> findByUserIdOrderByDateDebutAsc(Long userId);
    Optional<UserAbonnement> findTopByUserIdOrderByDateFinDesc(Long userId);
    long deleteByDateFinBefore(LocalDate date);
}
