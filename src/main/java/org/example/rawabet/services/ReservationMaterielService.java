package org.example.rawabet.services;

import org.example.rawabet.entities.Materiel;
import org.example.rawabet.entities.ReservationMateriel;
import org.example.rawabet.entities.User;
import org.example.rawabet.enums.MaterielStatus;
import org.example.rawabet.enums.ReservationStatus;
import org.example.rawabet.repositories.MaterielRepository;
import org.example.rawabet.repositories.ReservationMaterielRepository;
import org.example.rawabet.repositories.UserRepository;
import org.example.rawabet.services.IReservationMaterielService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationMaterielService implements IReservationMaterielService {

    @Autowired private ReservationMaterielRepository reservationMaterielRepository;
    @Autowired private MaterielRepository materielRepository;
    @Autowired private UserRepository userRepository;

    @Override
    public ReservationMateriel addReservation(ReservationMateriel rm) {
        return reservationMaterielRepository.save(rm);
    }

    @Override
    public ReservationMateriel updateReservation(ReservationMateriel rm) {
        if (!reservationMaterielRepository.existsById(rm.getId()))
            throw new RuntimeException("Réservation introuvable avec l'id: " + rm.getId());
        return reservationMaterielRepository.save(rm);
    }

    @Override
    public void deleteReservation(Long id) {
        if (!reservationMaterielRepository.existsById(id))
            throw new RuntimeException("Réservation introuvable avec l'id: " + id);
        reservationMaterielRepository.deleteById(id);
    }

    @Override
    public ReservationMateriel getById(Long id) {
        return reservationMaterielRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable avec l'id: " + id));
    }

    @Override
    public List<ReservationMateriel> getAll() {
        return reservationMaterielRepository.findAll();
    }

    @Override
    public ReservationMateriel reserverMateriel(Long userId, Long materielId, int quantite,
                                                LocalDateTime dateDebut, LocalDateTime dateFin) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec l'id: " + userId));

        Materiel materiel = materielRepository.findById(materielId)
                .orElseThrow(() -> new RuntimeException("Matériel introuvable avec l'id: " + materielId));

        if (dateFin.isBefore(dateDebut))
            throw new RuntimeException("La date de fin doit être après la date de début");

        if (dateDebut.isBefore(LocalDateTime.now()))
            throw new RuntimeException("La date de début doit être dans le futur");

        if (!materiel.isDisponible() || materiel.getStatus() != MaterielStatus.ACTIVE)
            throw new RuntimeException("Ce matériel n'est pas disponible");

        // ✅ Fixed: subtract both reservations and event assignments
        int reservedByReservations = materielRepository.getTotalReservedByReservation(
                materielId, dateDebut, dateFin);
        int assignedToEvents = materielRepository.getTotalAssignedByEvenement(
                materielId, dateDebut, dateFin);
        int available = materiel.getQuantiteDisponible() - reservedByReservations - assignedToEvents;

        if (available < quantite)
            throw new RuntimeException("Quantité insuffisante pour cette période. Disponible: " + available);

        ReservationMateriel rm = new ReservationMateriel();
        rm.setUser(user);
        rm.setMateriel(materiel);
        rm.setQuantite(quantite);
        rm.setDateDebut(dateDebut);
        rm.setDateFin(dateFin);
        rm.setStatut(ReservationStatus.PENDING);

        return reservationMaterielRepository.save(rm);
    }

    @Override
    public ReservationMateriel annulerReservation(Long reservationId) {
        ReservationMateriel rm = getById(reservationId);
        if (rm.getStatut() == ReservationStatus.CANCELLED)
            throw new RuntimeException("Cette réservation est déjà annulée");
        rm.setStatut(ReservationStatus.CANCELLED);
        return reservationMaterielRepository.save(rm);
    }

    @Override
    public ReservationMateriel confirmerReservation(Long reservationId) {
        ReservationMateriel rm = getById(reservationId);
        if (rm.getStatut() == ReservationStatus.CANCELLED)
            throw new RuntimeException("Impossible de confirmer une réservation annulée");
        if (rm.getStatut() == ReservationStatus.CONFIRMED)
            throw new RuntimeException("Cette réservation est déjà confirmée");
        rm.setStatut(ReservationStatus.CONFIRMED);
        return reservationMaterielRepository.save(rm);
    }

    @Override
    public List<ReservationMateriel> getReservationsByUser(Long userId) {
        return reservationMaterielRepository.findByUserId(userId);
    }

    @Override
    public List<ReservationMateriel> getReservationsByMateriel(Long materielId) {
        return reservationMaterielRepository.findByMaterielId(materielId);
    }

    @Override
    public List<ReservationMateriel> getReservationsByStatus(ReservationStatus statut) {
        return reservationMaterielRepository.findByStatut(statut);
    }

    @Override
    public List<ReservationMateriel> getOverlappingReservations(Long materielId,
                                                                LocalDateTime dateDebut,
                                                                LocalDateTime dateFin) {
        return reservationMaterielRepository.findOverlappingReservations(materielId, dateDebut, dateFin);
    }

    @Override
    public ReservationMateriel extendReservation(Long reservationId, LocalDateTime nouvelleDataFin) {
        ReservationMateriel rm = getById(reservationId);

        if (rm.getStatut() == ReservationStatus.CANCELLED)
            throw new RuntimeException("Impossible d'étendre une réservation annulée");

        if (nouvelleDataFin.isBefore(rm.getDateFin()))
            throw new RuntimeException("La nouvelle date de fin doit être après la date de fin actuelle");

        // ✅ Check availability for the extended period only
        int reservedByReservations = materielRepository.getTotalReservedByReservation(
                rm.getMateriel().getId(), rm.getDateFin(), nouvelleDataFin);
        int assignedToEvents = materielRepository.getTotalAssignedByEvenement(
                rm.getMateriel().getId(), rm.getDateFin(), nouvelleDataFin);
        // Subtract the current reservation's own quantity since it already holds the original slot
        int available = rm.getMateriel().getQuantiteDisponible()
                - reservedByReservations - assignedToEvents;

        if (available < rm.getQuantite())
            throw new RuntimeException("Stock insuffisant pour la période d'extension. Disponible: " + available);

        rm.setDateFin(nouvelleDataFin);
        return reservationMaterielRepository.save(rm);
    }

    @Override
    public ReservationMateriel retourPartiel(Long reservationId, int quantiteRetournee) {
        ReservationMateriel rm = getById(reservationId);

        if (rm.getStatut() == ReservationStatus.CANCELLED)
            throw new RuntimeException("Cette réservation est déjà annulée");

        if (quantiteRetournee >= rm.getQuantite())
            throw new RuntimeException(
                    "Pour un retour total, utilisez annulerReservation. " +
                            "Quantité actuelle: " + rm.getQuantite());

        if (quantiteRetournee <= 0)
            throw new RuntimeException("La quantité retournée doit être supérieure à 0");

        // ✅ Reduce quantity — the freed stock is automatically available for others
        rm.setQuantite(rm.getQuantite() - quantiteRetournee);
        return reservationMaterielRepository.save(rm);
    }
}