package org.example.rawabet.repositories;

import org.example.rawabet.entities.CarteFidelite;
import org.example.rawabet.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarteFideliteRepository extends JpaRepository<CarteFidelite, Long> {

    // 🔥 OBLIGATOIRE
    Optional<CarteFidelite> findByUser(User user);

}