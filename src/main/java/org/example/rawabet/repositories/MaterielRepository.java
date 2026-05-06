package org.example.rawabet.repositories;

import org.example.rawabet.entities.Materiel;
import org.example.rawabet.enums.MaterielStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MaterielRepository extends JpaRepository<Materiel, Long> {

    List<Materiel> findByCategorieId(Long categorieId);
    List<Materiel> findByStatus(MaterielStatus status);
}