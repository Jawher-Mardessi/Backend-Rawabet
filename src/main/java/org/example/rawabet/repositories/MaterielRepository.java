package org.example.rawabet.repositories;

import org.example.rawabet.entities.Materiel;
import org.example.rawabet.enums.MaterielStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface MaterielRepository extends JpaRepository<Materiel, Long> {

    List<Materiel> findByCategorieId(Long categorieId);
    List<Materiel> findByDisponibleTrue();
    List<Materiel> findByStatus(MaterielStatus status);               // ✅ filter by status

    // ✅ Fixed: deducts BOTH standalone reservations AND event assignments
    @Query("SELECT COALESCE(SUM(r.quantite), 0) FROM ReservationMateriel r " +
            "WHERE r.materiel.id = :materielId " +
            "AND r.statut <> 'ANNULEE' " +
            "AND r.dateDebut < :dateFin AND r.dateFin > :dateDebut")
    int getTotalReservedByReservation(
            @Param("materielId") Long materielId,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin);

    // ✅ Fixed: quantity assigned to events that overlap the period
    @Query("SELECT COALESCE(SUM(em.quantite), 0) FROM EvenementMateriel em " +
            "WHERE em.materiel.id = :materielId " +
            "AND em.evenement.dateDebut < :dateFin " +
            "AND em.evenement.dateFin > :dateDebut")
    int getTotalAssignedByEvenement(
            @Param("materielId") Long materielId,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin);
}