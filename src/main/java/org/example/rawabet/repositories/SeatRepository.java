package org.example.rawabet.repositories;

import org.example.rawabet.entities.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    Optional<Seat> findByNumeroAndSeanceId(int numero, Long seanceId);
    List<Seat> findBySeanceId(Long seanceId);
}
