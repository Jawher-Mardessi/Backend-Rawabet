package org.example.rawabet.repositories;

import org.example.rawabet.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ── Recherche par email ────────────────────────────────────────────────
    Optional<User> findByEmail(String email);
    List<User> findDistinctByRoles_Id(Long roleId);

    Optional<User> findByEmailIgnoreCase(String email);

    // ── Pagination ─────────────────────────────────────────────────────────
    Page<User> findAll(Pageable pageable);

    // ── Recherche texte ────────────────────────────────────────────────────
    @Query("""
        SELECT u FROM User u
        WHERE u.isActive = true
          AND (LOWER(u.nom)   LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')))
    """)
    List<User> searchByNomOrEmail(@Param("query") String query);

    // ── Chargement optimisé (évite N+1) ───────────────────────────────────
    @Query("""
        SELECT DISTINCT u FROM User u
        LEFT JOIN FETCH u.carteFidelite
        LEFT JOIN FETCH u.roles
    """)
    List<User> findAllWithCarteAndRoles();

    // ── Statistiques ───────────────────────────────────────────────────────
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :roleName")
    long countByRoleName(@Param("roleName") String roleName);

    // ── Utilisateurs actifs sauf un ────────────────────────────────────────
    @Query("SELECT u FROM User u WHERE u.id <> :excludeId AND u.isActive = true")
    List<User> findAllActiveExcept(@Param("excludeId") Long excludeId);

    // ════════════════════════════════════════════════════════════════════════
    // Requêtes pour BanExpiryScheduler
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Bans temporaires expirés — isActive=false ET banUntil dans le passé.
     */
    @Query("SELECT u FROM User u WHERE u.isActive = false " +
            "AND u.banUntil IS NOT NULL AND u.banUntil < :now")
    List<User> findByIsActiveFalseAndBanUntilBefore(@Param("now") LocalDateTime now);

    /**
     * Verrous de connexion expirés.
     */
    @Query("SELECT u FROM User u WHERE u.loginLockedUntil IS NOT NULL " +
            "AND u.loginLockedUntil < :now")
    List<User> findByLoginLockedUntilBefore(@Param("now") LocalDateTime now);
}
