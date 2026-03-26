package org.example.rawabet.services;

import org.example.rawabet.entities.Materiel;
import org.example.rawabet.repositories.MaterielRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MaterielService implements IMaterielService {

    @Autowired
    private MaterielRepository materielRepository;

    @Override
    public Materiel addMateriel(Materiel materiel) {
        return materielRepository.save(materiel);
    }

    @Override
    public Materiel updateMateriel(Materiel materiel) {
        return materielRepository.save(materiel);
    }

    @Override
    public void deleteMateriel(Long id) {
        materielRepository.deleteById(id);
    }

    @Override
    public Materiel getMaterielById(Long id) {
        Optional<Materiel> materiel = materielRepository.findById(id);
        return materiel.orElse(null);
    }

    @Override
    public List<Materiel> getAllMateriels() {
        return materielRepository.findAll();
    }
}
