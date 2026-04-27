package org.example.rawabet.repositories;

import org.example.rawabet.entities.UserAbonnement;
import org.example.rawabet.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserAbonnementRepository extends JpaRepository<UserAbonnement, Long> {

    Optional<UserAbonnement> findByUserId(Long userId);

    // DEPRECATED: Use status-based queries instead
    // @Deprecated
    // long deleteByDateFinBefore(LocalDate date);

    // Find all subscriptions for a user, sorted by dateDebut ascending
    List<UserAbonnement> findByUserIdOrderByDateDebutAsc(Long userId);

    // Find active subscription for a user (today >= dateDebut and today <= dateFin)
    @Query("SELECT ua FROM UserAbonnement ua WHERE ua.user.id = :userId AND ua.dateDebut <= :today AND ua.dateFin >= :today AND ua.status = 'ACTIVE' ORDER BY ua.dateDebut DESC LIMIT 1")
    Optional<UserAbonnement> findActiveSubscriptionForUser(@Param("userId") Long userId, @Param("today") LocalDate today);

    // Find only QUEUED subscriptions for a user, sorted by dateDebut ascending
    @Query("SELECT ua FROM UserAbonnement ua WHERE ua.user.id = :userId AND ua.status = 'QUEUED' ORDER BY ua.dateDebut ASC")
    List<UserAbonnement> findQueuedSubscriptionsForUser(@Param("userId") Long userId);

    // Find subscriptions with specific status
    List<UserAbonnement> findByUserIdAndStatus(Long userId, SubscriptionStatus status);
}

