package org.example.rawabet.repositories;

import org.example.rawabet.entities.ReservationEvenement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationEvenementRepository extends JpaRepository<ReservationEvenement, Long> {
}