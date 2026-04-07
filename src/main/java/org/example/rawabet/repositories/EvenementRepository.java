package org.example.rawabet.repositories;

import org.example.rawabet.entities.Evenement;
import org.example.rawabet.enums.EvenementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface EvenementRepository extends JpaRepository<Evenement, Long> {

    List<Evenement> findByDateDebutAfter(LocalDateTime now);
    List<Evenement> findBySalleId(Long salleId);
    List<Evenement> findByDateDebutBetween(LocalDateTime dateDebut, LocalDateTime dateFin);
    List<Evenement> findByStatus(EvenementStatus status);              // ✅ filter by status

    // ✅ Search by keyword in titre or description
    @Query("SELECT e FROM Evenement e WHERE " +
            "LOWER(e.titre) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Evenement> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT COUNT(r) FROM ReservationEvenement r " +
            "WHERE r.evenement.id = :evenementId " +
            "AND r.statut <> 'ANNULEE' " +
            "AND r.enAttente = false")
    int countActiveReservations(@Param("evenementId") Long evenementId);

    // ✅ Count waitlist entries for an event
    @Query("SELECT COUNT(r) FROM ReservationEvenement r " +
            "WHERE r.evenement.id = :evenementId " +
            "AND r.enAttente = true " +
            "AND r.statut <> 'ANNULEE'")
    int countWaitlist(@Param("evenementId") Long evenementId);
}