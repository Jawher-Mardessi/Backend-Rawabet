package org.example.rawabet.repositories;

import jakarta.persistence.LockModeType;
import org.example.rawabet.entities.CarteFidelite;
import org.example.rawabet.entities.User;
import org.example.rawabet.enums.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CarteFideliteRepository extends JpaRepository<CarteFidelite, Long> {

    Optional<CarteFidelite> findByUser(User user);

    // CORRECTION — verrou pessimiste pour le transfert de points
    // Avant : deux transferts simultanés pouvaient passer le check "points suffisants"
    // en même temps et créer un solde négatif.
    // Ce @Lock force un SELECT ... FOR UPDATE : le 2ème thread attend que
    // le 1er commit avant de lire les points, garantissant la cohérence.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CarteFidelite c WHERE c.user = :user")
    Optional<CarteFidelite> findByUserWithLock(User user);

    long countByLevel(Level level);

    List<CarteFidelite> findTop10ByOrderByPointsDesc();

    @Query("SELECT COALESCE(SUM(c.points), 0) FROM CarteFidelite c")
    long sumAllPoints();
}