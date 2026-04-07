package org.example.rawabet.repositories;

import org.example.rawabet.entities.ReservationEvenement;
import org.example.rawabet.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationEvenementRepository extends JpaRepository<ReservationEvenement, Long> {

    List<ReservationEvenement> findByUserId(Long userId);
    List<ReservationEvenement> findByEvenementId(Long evenementId);
    List<ReservationEvenement> findByStatut(ReservationStatus statut);

    boolean existsByUserIdAndEvenementIdAndStatutNot(
            Long userId, Long evenementId, ReservationStatus statut);

    // ✅ Find expired pending reservations to auto-cancel
    @Query("SELECT r FROM ReservationEvenement r " +
            "WHERE r.statut = 'EN_ATTENTE' " +
            "AND r.enAttente = false " +
            "AND r.dateExpiration < :now")
    List<ReservationEvenement> findExpiredPendingReservations(@Param("now") LocalDateTime now);

    // ✅ Find first waitlist entry for an event (oldest first)
    @Query("SELECT r FROM ReservationEvenement r " +
            "WHERE r.evenement.id = :evenementId " +
            "AND r.enAttente = true " +
            "AND r.statut <> 'ANNULEE' " +
            "ORDER BY r.dateReservation ASC")
    List<ReservationEvenement> findWaitlistByEvenement(@Param("evenementId") Long evenementId);

    // ✅ Get waitlist for a specific user
    List<ReservationEvenement> findByUserIdAndEnAttenteTrue(Long userId);
}