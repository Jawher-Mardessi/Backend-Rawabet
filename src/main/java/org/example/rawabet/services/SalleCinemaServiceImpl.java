package org.example.rawabet.services;

import org.example.rawabet.entities.SalleCinema;
import org.example.rawabet.repositories.SalleCinemaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class SalleCinemaServiceImpl implements ISalleCinemaService {

    private final SalleCinemaRepository salleCinemaRepository;

    public SalleCinemaServiceImpl(SalleCinemaRepository salleCinemaRepository) {
        this.salleCinemaRepository = salleCinemaRepository;
    }

    @Override
    public SalleCinema addSalle(SalleCinema salle) {
        return salleCinemaRepository.save(salle);
    }

    @Override
    public SalleCinema updateSalle(SalleCinema salle) {
        return salleCinemaRepository.save(salle);
    }

    @Override
    public void deleteSalle(Long id) {
        salleCinemaRepository.deleteById(id);
    }

    @Override
    public SalleCinema getSalleById(Long id) {
        return salleCinemaRepository.findById(id).orElse(null);
    }

    @Override
    public List<SalleCinema> getAllSalles() {
        return salleCinemaRepository.findAll();
    }

}