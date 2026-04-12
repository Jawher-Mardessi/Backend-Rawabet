package org.example.rawabet.cinema.repositories;


import org.example.rawabet.cinema.entities.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CinemaRepository extends JpaRepository<Cinema, Long> {

    List<Cinema> findByIsActiveTrue();

    Optional<Cinema> findBySlug(String slug);

}