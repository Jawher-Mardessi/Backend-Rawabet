package org.example.rawabet.repositories;

import org.example.rawabet.entities.EvenementMateriel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EvenementMaterielRepository extends JpaRepository<EvenementMateriel, Long> {

    List<EvenementMateriel> findByEvenementId(Long evenementId);
    List<EvenementMateriel> findByMaterielId(Long materielId);
}