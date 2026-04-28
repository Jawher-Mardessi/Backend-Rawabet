package org.example.rawabet.cinema.repositories;

import org.example.rawabet.cinema.entities.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    // Méthodes du main
    List<Seat> findByRowIdAndIsActiveTrue(Long rowId);

    List<Seat> findByRowSalleId(Long salleId);

    @Modifying
    @Query("UPDATE Seat s SET s.isActive = false WHERE s.row.salle.id = :salleId")
    void disableAllBySalleId(@Param("salleId") Long salleId);

    // Tes méthodes
    List<Seat> findBySeanceId(Long seanceId);

    Optional<Seat> findBySeatNumberAndSeanceId(Integer seatNumber, Long seanceId);
}