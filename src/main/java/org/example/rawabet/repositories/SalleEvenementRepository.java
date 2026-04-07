package org.example.rawabet.repositories;

import org.example.rawabet.entities.SalleEvenement;
import org.example.rawabet.enums.SalleStatus;
import org.example.rawabet.enums.SalleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface SalleEvenementRepository extends JpaRepository<SalleEvenement, Long> {

    List<SalleEvenement> findByType(SalleType type);                    // ✅ filter by type
    List<SalleEvenement> findByStatus(SalleStatus status);              // ✅ filter by status

    @Query("SELECT s FROM SalleEvenement s WHERE s.status = 'ACTIVE' AND s.id NOT IN (" +
            "SELECT e.salle.id FROM Evenement e " +
            "WHERE e.dateDebut < :dateFin AND e.dateFin > :dateDebut)")
    List<SalleEvenement> findAvailableSalles(
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin);

    @Query("SELECT COUNT(e) > 0 FROM Evenement e " +
            "WHERE e.salle.id = :salleId " +
            "AND e.dateDebut < :dateFin AND e.dateFin > :dateDebut")
    boolean isSalleOccupied(
            @Param("salleId") Long salleId,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin);
}