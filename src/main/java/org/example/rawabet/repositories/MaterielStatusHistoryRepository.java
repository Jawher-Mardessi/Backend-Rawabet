package org.example.rawabet.repositories;

import org.example.rawabet.entities.MaterielStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MaterielStatusHistoryRepository extends JpaRepository<MaterielStatusHistory, Long> {

    List<MaterielStatusHistory> findByMaterielId(Long materielId);

    List<MaterielStatusHistory> findByMaterielName(String materielName);

    List<MaterielStatusHistory> findAllByOrderByChangedAtDesc();

    Optional<MaterielStatusHistory> findFirstByMaterielIdOrderByChangedAtDesc(Long materielId);
}

