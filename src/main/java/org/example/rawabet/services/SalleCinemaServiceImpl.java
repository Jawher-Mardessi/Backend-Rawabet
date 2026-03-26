package org.example.rawabet.services;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.entities.SalleCinema;
import org.example.rawabet.repositories.SalleCinemaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class SalleCinemaServiceImpl implements ISalleCinemaService {

    private final SalleCinemaRepository salleCinemaRepository;

    // CREATE
    @Override
    public SalleCinema addSalle(SalleCinema salle) {
        return salleCinemaRepository.save(salle);
    }

    // UPDATE
    @Override
    public SalleCinema updateSalle(SalleCinema salle) {
        return salleCinemaRepository.save(salle);
    }

    // DELETE
    @Override
    public void deleteSalle(Long id) {
        salleCinemaRepository.deleteById(id);
    }

    // GET BY ID
    @Override
    public SalleCinema getSalleById(Long id) {
        return salleCinemaRepository.findById(id)
                .orElse(null);
    }

    // GET ALL
    @Override
    public List<SalleCinema> getAllSalles() {
        return salleCinemaRepository.findAll();
    }

}