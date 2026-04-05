package org.example.rawabet.repositories.cinema;


import org.example.rawabet.entities.cinema.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CinemaRepository extends JpaRepository<Cinema, Long> {

    List<Cinema> findByIsActiveTrue();

    Optional<Cinema> findBySlug(String slug);

}