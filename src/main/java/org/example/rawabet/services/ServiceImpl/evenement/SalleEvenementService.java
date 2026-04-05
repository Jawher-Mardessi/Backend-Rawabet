package org.example.rawabet.services.ServiceImpl.evenement;

import org.example.rawabet.entities.SalleEvenement;
import org.example.rawabet.repositories.SalleEvenementRepository;
import org.example.rawabet.services.IService.evenement.ISalleEvenementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SalleEvenementService implements ISalleEvenementService {

    @Autowired
    private SalleEvenementRepository salleEvenementRepository;

    @Override
    public SalleEvenement addSalle(SalleEvenement salle) {
        return salleEvenementRepository.save(salle);
    }

    @Override
    public SalleEvenement updateSalle(SalleEvenement salle) {
        return salleEvenementRepository.save(salle);
    }

    @Override
    public void deleteSalle(Long id) {
        salleEvenementRepository.deleteById(id);
    }

    @Override
    public SalleEvenement getSalleById(Long id) {
        Optional<SalleEvenement> salle = salleEvenementRepository.findById(id);
        return salle.orElse(null);
    }

    @Override
    public List<SalleEvenement> getAllSalles() {
        return salleEvenementRepository.findAll();
    }
}
