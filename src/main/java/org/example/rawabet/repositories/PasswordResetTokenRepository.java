package org.example.rawabet.repositories;

import org.example.rawabet.entities.PasswordResetToken;
import org.example.rawabet.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    // ✅ supprimer les anciens tokens du même user
    void deleteByUser(User user);
}