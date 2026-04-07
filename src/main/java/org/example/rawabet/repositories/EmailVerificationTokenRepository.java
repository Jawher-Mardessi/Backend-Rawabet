package org.example.rawabet.repositories;

import org.example.rawabet.entities.EmailVerificationToken;
import org.example.rawabet.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

    Optional<EmailVerificationToken> findByToken(String token);

    void deleteByUser(User user);
}