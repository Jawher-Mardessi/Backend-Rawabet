package org.example.rawabet.services;

import org.example.rawabet.entities.Seance;
import org.example.rawabet.repositories.SeanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeanceServiceImpl implements ISeanceService {

    @Autowired
    private SeanceRepository seanceRepository;

    @Override
    public Seance addSeance(Seance seance) {
        return seanceRepository.save(seance);
    }

    @Override
    public Seance updateSeance(Seance seance) {
        return seanceRepository.save(seance);
    }

    @Override
    public void deleteSeance(Long id) {
        seanceRepository.deleteById(id);
    }

    @Override
    public Seance getSeanceById(Long id) {
        return seanceRepository.findById(id).orElse(null);
    }

    @Override
    public List<Seance> getAllSeances() {
        return seanceRepository.findAll();
    }
}