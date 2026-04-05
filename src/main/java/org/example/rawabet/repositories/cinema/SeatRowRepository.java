package org.example.rawabet.repositories.cinema;

import org.example.rawabet.entities.cinema.SeatRow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRowRepository extends JpaRepository<SeatRow, Long> {

    List<SeatRow> findBySalleIdOrderByDisplayOrder(Long salleId);

}
