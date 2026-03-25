package org.example.rawabet.repositories;

import org.example.rawabet.entities.ReservationMateriel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationMaterielRepository extends JpaRepository<ReservationMateriel, Long> {
}