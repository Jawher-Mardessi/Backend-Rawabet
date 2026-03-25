package org.example.rawabet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.rawabet.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
}