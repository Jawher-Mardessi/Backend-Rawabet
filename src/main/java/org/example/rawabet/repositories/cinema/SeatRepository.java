package org.example.rawabet.repositories.cinema;

import org.example.rawabet.entities.cinema.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByRowIdAndIsActiveTrue(Long rowId);

    List<Seat> findByRowSalleId(Long salleId);

}