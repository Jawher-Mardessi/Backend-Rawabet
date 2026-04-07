package org.example.rawabet.services;

import org.example.rawabet.entities.SalleEvenement;
import org.example.rawabet.enums.SalleStatus;
import org.example.rawabet.enums.SalleType;
import org.example.rawabet.repositories.SalleEvenementRepository;
import org.example.rawabet.services.ISalleEvenementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SalleEvenementService implements ISalleEvenementService {

    @Autowired
    private SalleEvenementRepository salleRepository;

    @Override
    public SalleEvenement addSalle(SalleEvenement salle) {
        if (salle.getStatus() == null) salle.setStatus(SalleStatus.ACTIVE);
        return salleRepository.save(salle);
    }

    @Override
    public SalleEvenement updateSalle(SalleEvenement salle) {
        if (!salleRepository.existsById(salle.getId()))
            throw new RuntimeException("Salle introuvable avec l'id: " + salle.getId());
        return salleRepository.save(salle);
    }

    @Override
    public void deleteSalle(Long id) {
        if (!salleRepository.existsById(id))
            throw new RuntimeException("Salle introuvable avec l'id: " + id);
        salleRepository.deleteById(id);
    }

    @Override
    public SalleEvenement getSalleById(Long id) {
        return salleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Salle introuvable avec l'id: " + id));
    }

    @Override
    public List<SalleEvenement> getAllSalles() {
        return salleRepository.findAll();
    }

    @Override
    public boolean isSalleAvailable(Long salleId, LocalDateTime dateDebut, LocalDateTime dateFin) {
        SalleEvenement salle = getSalleById(salleId);
        if (salle.getStatus() == SalleStatus.MAINTENANCE)
            return false;
        return !salleRepository.isSalleOccupied(salleId, dateDebut, dateFin);
    }

    @Override
    public List<SalleEvenement> getAvailableSalles(LocalDateTime dateDebut, LocalDateTime dateFin) {
        return salleRepository.findAvailableSalles(dateDebut, dateFin);
    }

    @Override
    public List<SalleEvenement> getSallesByType(SalleType type) {
        return salleRepository.findByType(type);
    }

    @Override
    public SalleEvenement updateStatus(Long id, SalleStatus status) {
        SalleEvenement salle = getSalleById(id);
        salle.setStatus(status);
        return salleRepository.save(salle);
    }
}