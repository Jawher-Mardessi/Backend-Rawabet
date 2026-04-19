package org.example.rawabet.repositories;

import org.example.rawabet.entities.Abonnement;
import org.example.rawabet.enums.AbonnementType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AbonnementRepository extends JpaRepository<Abonnement, Long> {
    List<Abonnement> findByType(AbonnementType type);
}
