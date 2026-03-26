package org.example.rawabet.repositories;

import org.example.rawabet.entities.Abonnement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AbonnementRepository extends JpaRepository<Abonnement, Long> {
}