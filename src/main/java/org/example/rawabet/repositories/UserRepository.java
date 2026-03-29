package org.example.rawabet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.rawabet.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

}