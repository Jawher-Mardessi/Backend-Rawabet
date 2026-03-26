package org.example.rawabet.services;

import org.example.rawabet.entities.SalleCinema;

import java.util.List;

public interface ISalleCinemaService {

    SalleCinema addSalle(SalleCinema salle);

    SalleCinema updateSalle(SalleCinema salle);

    void deleteSalle(Long id);

    SalleCinema getSalleById(Long id);

    List<SalleCinema> getAllSalles();
}
