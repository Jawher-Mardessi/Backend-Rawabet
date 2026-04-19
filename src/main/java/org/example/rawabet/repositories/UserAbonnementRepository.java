package org.example.rawabet.repositories;

import jakarta.persistence.LockModeType;
import org.example.rawabet.entities.User;
import org.example.rawabet.entities.UserAbonnement;
import org.example.rawabet.enums.UserAbonnementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserAbonnementRepository extends JpaRepository<UserAbonnement, Long> {

    /** Returns all subscriptions for a user, ordered chronologically. */
    List<UserAbonnement> findByUserOrderByDateDebutAsc(User user);

    /** Returns subscriptions for a user filtered by status. */
    List<UserAbonnement> findByUserAndStatusOrderByDateDebutAsc(User user, UserAbonnementStatus status);

    /** Returns the subscription with the latest end-date for a user (for chaining). */
    Optional<UserAbonnement> findTopByUserOrderByDateFinDesc(User user);

    /**
     * Acquires a pessimistic write lock on all subscriptions of a user.
     * Used inside a transaction to prevent concurrent overlapping inserts.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ua FROM UserAbonnement ua WHERE ua.user = :user")
    List<UserAbonnement> findByUserForUpdate(@Param("user") User user);

    /** Returns active or queued subscriptions for a user. */
    @Query("SELECT ua FROM UserAbonnement ua WHERE ua.user = :user " +
           "AND ua.status IN (org.example.rawabet.enums.UserAbonnementStatus.ACTIVE, " +
           "                  org.example.rawabet.enums.UserAbonnementStatus.QUEUED)")
    List<UserAbonnement> findActiveOrQueuedByUser(@Param("user") User user);
}
