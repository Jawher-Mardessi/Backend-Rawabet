package org.example.rawabet.repositories;

import org.example.rawabet.entities.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository
        extends JpaRepository<Seat,Long>{

    List<Seat> findBySeanceId(Long seanceId);

    boolean existsByIdAndReservationIsNotNull(Long id);

}