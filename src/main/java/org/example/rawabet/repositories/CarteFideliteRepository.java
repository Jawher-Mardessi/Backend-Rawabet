package org.example.rawabet.repositories;

import org.example.rawabet.entities.CarteFidelite;
import org.example.rawabet.entities.User;
import org.example.rawabet.enums.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CarteFideliteRepository extends JpaRepository<CarteFidelite, Long> {

    Optional<CarteFidelite> findByUser(User user);

    // ✅ compter par level
    long countByLevel(Level level);

    // ✅ top 10 par points
    List<CarteFidelite> findTop10ByOrderByPointsDesc();

    // ✅ total points distribués
    @Query("SELECT COALESCE(SUM(c.points), 0) FROM CarteFidelite c")
    long sumAllPoints();
}