package org.example.rawabet.cinema.repositories;


import org.example.rawabet.cinema.entities.Film;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FilmRepository extends JpaRepository<Film, Long> {

    List<Film> findByIsActiveTrue();

    Optional<Film> findByImdbId(String imdbId);

}