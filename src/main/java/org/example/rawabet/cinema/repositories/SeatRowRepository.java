package org.example.rawabet.cinema.repositories;

import org.example.rawabet.cinema.entities.SeatRow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRowRepository extends JpaRepository<SeatRow, Long> {

    List<SeatRow> findBySalleIdOrderByDisplayOrder(Long salleId);

}
