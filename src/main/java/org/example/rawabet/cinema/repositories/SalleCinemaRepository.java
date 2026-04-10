package org.example.rawabet.cinema.repositories;

import org.example.rawabet.cinema.entities.SalleCinema;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalleCinemaRepository extends JpaRepository<SalleCinema, Long> {

    List<SalleCinema> findByCinemaIdAndIsActiveTrue(Long cinemaId);

}