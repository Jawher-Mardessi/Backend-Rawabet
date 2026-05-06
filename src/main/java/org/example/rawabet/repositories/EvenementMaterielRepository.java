package org.example.rawabet.repositories;

import org.example.rawabet.entities.EvenementMateriel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EvenementMaterielRepository extends JpaRepository<EvenementMateriel, Long> {

    List<EvenementMateriel> findByEvenementId(Long evenementId);
    List<EvenementMateriel> findByMaterielId(Long materielId);

    @Query("SELECT em FROM EvenementMateriel em JOIN em.evenement e " +
            "WHERE em.materiel.id = :materielId " +
            "AND e.dateDebut < :dateFin AND e.dateFin > :dateDebut")
    List<EvenementMateriel> findOverlappingEventMateriels(
            @Param("materielId") Long materielId,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin);
}