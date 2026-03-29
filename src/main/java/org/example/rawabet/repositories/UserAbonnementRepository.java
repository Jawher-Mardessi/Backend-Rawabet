package org.example.rawabet.repositories;

import org.example.rawabet.entities.UserAbonnement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface UserAbonnementRepository extends JpaRepository<UserAbonnement, Long> {
    Optional<UserAbonnement> findByUserId(Long userId);
    long deleteByDateFinBefore(LocalDate date);
}
