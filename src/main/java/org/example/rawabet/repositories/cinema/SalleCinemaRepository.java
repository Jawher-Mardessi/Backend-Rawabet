package org.example.rawabet.repositories.cinema;

import org.example.rawabet.entities.cinema.SalleCinema;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalleCinemaRepository extends JpaRepository<SalleCinema, Long> {

    List<SalleCinema> findByCinemaIdAndIsActiveTrue(Long cinemaId);

}