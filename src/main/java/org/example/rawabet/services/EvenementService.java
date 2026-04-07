package org.example.rawabet.services;

import org.example.rawabet.entities.Evenement;
import org.example.rawabet.entities.EvenementMateriel;
import org.example.rawabet.entities.Materiel;
import org.example.rawabet.entities.SalleEvenement;
import org.example.rawabet.enums.EvenementStatus;
import org.example.rawabet.enums.SalleStatus;
import org.example.rawabet.repositories.*;
import org.example.rawabet.services.IEvenementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EvenementService implements IEvenementService {

    @Autowired private EvenementRepository evenementRepository;
    @Autowired private SalleEvenementRepository salleRepository;
    @Autowired private MaterielRepository materielRepository;
    @Autowired private EvenementMaterielRepository evenementMaterielRepository;

    @Override
    public Evenement addEvenement(Evenement evenement) {
        if (evenement.getDateFin().isBefore(evenement.getDateDebut()))
            throw new RuntimeException("La date de fin doit être après la date de début");
        if (evenement.getStatus() == null)
            evenement.setStatus(EvenementStatus.DRAFT);
        if (evenement.getSalle() != null) {
            SalleEvenement salle = salleRepository.findById(evenement.getSalle().getId())
                    .orElseThrow(() -> new RuntimeException("Salle introuvable"));
            if (salle.getStatus() == SalleStatus.MAINTENANCE)
                throw new RuntimeException("La salle est en maintenance");
            if (salleRepository.isSalleOccupied(salle.getId(),
                    evenement.getDateDebut(), evenement.getDateFin()))
                throw new RuntimeException("La salle est déjà occupée pour ces dates");
        }
        return evenementRepository.save(evenement);
    }

    @Override
    public Evenement updateEvenement(Evenement evenement) {
        if (!evenementRepository.existsById(evenement.getId()))
            throw new RuntimeException("Événement introuvable avec l'id: " + evenement.getId());
        if (evenement.getDateFin().isBefore(evenement.getDateDebut()))
            throw new RuntimeException("La date de fin doit être après la date de début");
        return evenementRepository.save(evenement);
    }

    @Override
    public void deleteEvenement(Long id) {
        if (!evenementRepository.existsById(id))
            throw new RuntimeException("Événement introuvable avec l'id: " + id);
        evenementRepository.deleteById(id);
    }

    @Override
    public Evenement getEvenementById(Long id) {
        return evenementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Événement introuvable avec l'id: " + id));
    }

    @Override
    public List<Evenement> getAllEvenements() {
        return evenementRepository.findAll();
    }

    @Override
    public Evenement assignSalleToEvenement(Long evenementId, Long salleId) {
        Evenement evenement = getEvenementById(evenementId);
        SalleEvenement salle = salleRepository.findById(salleId)
                .orElseThrow(() -> new RuntimeException("Salle introuvable avec l'id: " + salleId));
        if (salle.getStatus() == SalleStatus.MAINTENANCE)
            throw new RuntimeException("La salle est en maintenance");
        if (salleRepository.isSalleOccupied(salleId,
                evenement.getDateDebut(), evenement.getDateFin()))
            throw new RuntimeException("La salle est déjà occupée pour ces dates");
        evenement.setSalle(salle);
        return evenementRepository.save(evenement);
    }

    @Override
    public EvenementMateriel assignMaterielToEvenement(Long evenementId, Long materielId, int quantite) {
        Evenement evenement = getEvenementById(evenementId);
        Materiel materiel = materielRepository.findById(materielId)
                .orElseThrow(() -> new RuntimeException("Matériel introuvable avec l'id: " + materielId));
        if (!materiel.isDisponible())
            throw new RuntimeException("Le matériel n'est pas disponible");
        // ✅ Check stock accounting for both reservations and other event assignments
        int reservedByReservations = materielRepository.getTotalReservedByReservation(
                materielId, evenement.getDateDebut(), evenement.getDateFin());
        int assignedToEvents = materielRepository.getTotalAssignedByEvenement(
                materielId, evenement.getDateDebut(), evenement.getDateFin());
        int available = materiel.getQuantiteDisponible() - reservedByReservations - assignedToEvents;
        if (available < quantite)
            throw new RuntimeException("Stock insuffisant pour cette période. Disponible: " + available);
        EvenementMateriel em = new EvenementMateriel();
        em.setEvenement(evenement);
        em.setMateriel(materiel);
        em.setQuantite(quantite);
        return evenementMaterielRepository.save(em);
    }

    @Override
    public void removeMaterielFromEvenement(Long evenementMaterielId) {
        if (!evenementMaterielRepository.existsById(evenementMaterielId))
            throw new RuntimeException("Assignment introuvable avec l'id: " + evenementMaterielId);
        evenementMaterielRepository.deleteById(evenementMaterielId);
    }

    @Override
    public List<EvenementMateriel> getMaterielsByEvenement(Long evenementId) {
        return evenementMaterielRepository.findByEvenementId(evenementId);
    }

    @Override
    public boolean hasAvailablePlaces(Long evenementId) {
        return getRemainingPlaces(evenementId) > 0;
    }

    @Override
    public int getRemainingPlaces(Long evenementId) {
        Evenement evenement = getEvenementById(evenementId);
        int activeReservations = evenementRepository.countActiveReservations(evenementId);
        return evenement.getNombreDePlaces() - activeReservations;
    }

    @Override
    public List<Evenement> getUpcomingEvenements() {
        return evenementRepository.findByDateDebutAfter(LocalDateTime.now());
    }

    @Override
    public List<Evenement> getEvenementsBySalle(Long salleId) {
        return evenementRepository.findBySalleId(salleId);
    }

    @Override
    public List<Evenement> getEvenementsByDateRange(LocalDateTime dateDebut, LocalDateTime dateFin) {
        return evenementRepository.findByDateDebutBetween(dateDebut, dateFin);
    }

    @Override
    public Evenement updateStatus(Long id, EvenementStatus status) {
        Evenement evenement = getEvenementById(id);
        evenement.setStatus(status);
        return evenementRepository.save(evenement);
    }

    @Override
    public List<Evenement> searchByKeyword(String keyword) {
        return evenementRepository.searchByKeyword(keyword);
    }

    @Override
    public List<Evenement> getEvenementsByStatus(EvenementStatus status) {
        return evenementRepository.findByStatus(status);
    }

    @Override
    public int getWaitlistCount(Long evenementId) {
        return evenementRepository.countWaitlist(evenementId);
    }
}