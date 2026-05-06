package org.example.rawabet.repositories;

import org.example.rawabet.entities.Seance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SeanceRepository extends JpaRepository<Seance, Long> {

    @Query("SELECT s FROM Seance s LEFT JOIN FETCH s.film LEFT JOIN FETCH s.salleCinema")
    List<Seance> findAll();

    @Query("SELECT s FROM Seance s LEFT JOIN FETCH s.film LEFT JOIN FETCH s.salleCinema WHERE s.id = :id")
    Optional<Seance> findById(Long id);
    @Query("SELECT s FROM Seance s LEFT JOIN FETCH s.film LEFT JOIN FETCH s.salleCinema")
    List<Seance> findAllWithFilmAndSalle();
}