package org.example.rawabet.repositories;

import org.example.rawabet.entities.PasswordResetToken;
import org.example.rawabet.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByUser(User user);

    // ✅ supprimer les anciens tokens du même user
    @Transactional
    void deleteByUser(User user);
}