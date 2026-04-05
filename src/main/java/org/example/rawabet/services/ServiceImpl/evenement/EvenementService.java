package org.example.rawabet.services.ServiceImpl.evenement;

import org.example.rawabet.entities.Evenement;
import org.example.rawabet.repositories.EvenementRepository;
import org.example.rawabet.services.IService.evenement.IEvenementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EvenementService implements IEvenementService {

    @Autowired
    private EvenementRepository evenementRepository;

    @Override
    public Evenement addEvenement(Evenement evenement) {
        return evenementRepository.save(evenement);
    }

    @Override
    public Evenement updateEvenement(Evenement evenement) {
        return evenementRepository.save(evenement);
    }

    @Override
    public void deleteEvenement(Long id) {
        evenementRepository.deleteById(id);
    }

    @Override
    public Evenement getEvenementById(Long id) {
        Optional<Evenement> evenement = evenementRepository.findById(id);
        return evenement.orElse(null);
    }

    @Override
    public List<Evenement> getAllEvenements() {
        return evenementRepository.findAll();
    }
}
