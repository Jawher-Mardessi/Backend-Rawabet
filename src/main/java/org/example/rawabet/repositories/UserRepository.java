package org.example.rawabet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.example.rawabet.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    // ✅ Fonctionne maintenant car roles et permissions sont des Set
    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.roles r " +
            "LEFT JOIN FETCH r.permissions " +
            "WHERE u.email = :email")
    Optional<User> findByEmailWithRolesAndPermissions(@Param("email") String email);
}