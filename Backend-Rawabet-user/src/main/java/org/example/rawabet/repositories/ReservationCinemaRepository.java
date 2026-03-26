package org.example.rawabet.repositories;

import org.example.rawabet.entities.ReservationCinema;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationCinemaRepository extends JpaRepository<ReservationCinema, Long> {
}