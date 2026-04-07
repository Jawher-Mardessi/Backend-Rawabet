package org.example.rawabet.services;

import org.example.rawabet.entities.Materiel;
import org.example.rawabet.enums.MaterielStatus;
import org.example.rawabet.repositories.CategorieMaterielRepository;
import org.example.rawabet.repositories.MaterielRepository;
import org.example.rawabet.services.IMaterielService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MaterielService implements IMaterielService {

    @Autowired private MaterielRepository materielRepository;
    @Autowired private CategorieMaterielRepository categorieRepository;

    @Override
    public Materiel addMateriel(Materiel materiel) {
        if (materiel.getStatus() == null) materiel.setStatus(MaterielStatus.ACTIVE);
        return materielRepository.save(materiel);
    }

    @Override
    public Materiel updateMateriel(Materiel materiel) {
        if (!materielRepository.existsById(materiel.getId()))
            throw new RuntimeException("Matériel introuvable avec l'id: " + materiel.getId());
        return materielRepository.save(materiel);
    }

    @Override
    public void deleteMateriel(Long id) {
        if (!materielRepository.existsById(id))
            throw new RuntimeException("Matériel introuvable avec l'id: " + id);
        materielRepository.deleteById(id);
    }

    @Override
    public Materiel getMaterielById(Long id) {
        return materielRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matériel introuvable avec l'id: " + id));
    }

    @Override
    public List<Materiel> getAllMateriels() {
        return materielRepository.findAll();
    }

    @Override
    public boolean isMaterielAvailable(Long materielId, int quantiteDemandee,
                                       LocalDateTime dateDebut, LocalDateTime dateFin) {
        return getAvailableQuantity(materielId, dateDebut, dateFin) >= quantiteDemandee;
    }

    @Override
    public int getAvailableQuantity(Long materielId, LocalDateTime dateDebut, LocalDateTime dateFin) {
        Materiel materiel = getMaterielById(materielId);
        // ✅ Fixed: subtract both standalone reservations and event assignments
        int reservedByReservations = materielRepository.getTotalReservedByReservation(
                materielId, dateDebut, dateFin);
        int assignedToEvents = materielRepository.getTotalAssignedByEvenement(
                materielId, dateDebut, dateFin);
        return materiel.getQuantiteDisponible() - reservedByReservations - assignedToEvents;
    }

    @Override
    public List<Materiel> getAvailableMateriels(LocalDateTime dateDebut, LocalDateTime dateFin) {
        return materielRepository.findByDisponibleTrue().stream()
                .filter(m -> m.getStatus() == MaterielStatus.ACTIVE)
                .filter(m -> getAvailableQuantity(m.getId(), dateDebut, dateFin) > 0)
                .toList();
    }

    @Override
    public List<Materiel> getMaterielsByCategorie(Long categorieId) {
        return materielRepository.findByCategorieId(categorieId);
    }

    @Override
    public Materiel toggleDisponible(Long id) {
        Materiel materiel = getMaterielById(id);
        materiel.setDisponible(!materiel.isDisponible());
        return materielRepository.save(materiel);
    }

    @Override
    public Materiel updateStatus(Long id, MaterielStatus status) {
        Materiel materiel = getMaterielById(id);
        materiel.setStatus(status);
        if (status == MaterielStatus.MAINTENANCE || status == MaterielStatus.DAMAGED)
            materiel.setDisponible(false);
        if (status == MaterielStatus.ACTIVE)
            materiel.setDisponible(true);
        return materielRepository.save(materiel);
    }

    @Override
    public List<Materiel> getMaterielsByStatus(MaterielStatus status) {
        return materielRepository.findByStatus(status);
    }
}