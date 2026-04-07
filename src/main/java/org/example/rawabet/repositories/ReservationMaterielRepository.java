package org.example.rawabet.repositories;

import org.example.rawabet.entities.ReservationMateriel;
import org.example.rawabet.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationMaterielRepository extends JpaRepository<ReservationMateriel, Long> {

    List<ReservationMateriel> findByUserId(Long userId);

    List<ReservationMateriel> findByMaterielId(Long materielId);

    List<ReservationMateriel> findByStatut(ReservationStatus statut);

    // Find all overlapping reservations for a materiel in a date range
    @Query("SELECT r FROM ReservationMateriel r " +
            "WHERE r.materiel.id = :materielId " +
            "AND r.statut <> 'ANNULEE' " +
            "AND r.dateDebut < :dateFin AND r.dateFin > :dateDebut")
    List<ReservationMateriel> findOverlappingReservations(
            @Param("materielId") Long materielId,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin);
}