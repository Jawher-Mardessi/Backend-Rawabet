package org.example.rawabet.cinema.repositories;

import org.example.rawabet.cinema.entities.SeatRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatRowRepository extends JpaRepository<SeatRow, Long> {

    List<SeatRow> findBySalleIdOrderByDisplayOrder(Long salleId);

    @Modifying
    @Query("DELETE FROM SeatRow r WHERE r.salle.id = :salleId")
    void deleteBySalleId(@Param("salleId") Long salleId);


}