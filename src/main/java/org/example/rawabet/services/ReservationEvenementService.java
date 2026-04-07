package org.example.rawabet.services;

import org.example.rawabet.entities.Evenement;
import org.example.rawabet.entities.ReservationEvenement;
import org.example.rawabet.entities.User;
import org.example.rawabet.enums.EvenementStatus;
import org.example.rawabet.enums.ReservationStatus;
import org.example.rawabet.repositories.EvenementRepository;
import org.example.rawabet.repositories.ReservationEvenementRepository;
import org.example.rawabet.repositories.UserRepository;
import org.example.rawabet.services.IReservationEvenementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationEvenementService implements IReservationEvenementService {

    private static final int EXPIRY_MINUTES = 30; // pending reservation expires after 30 min

    @Autowired private ReservationEvenementRepository reservationRepository;
    @Autowired private EvenementRepository evenementRepository;
    @Autowired private UserRepository userRepository;

    @Override
    public ReservationEvenement addReservation(ReservationEvenement reservation) {
        return reservationRepository.save(reservation);
    }

    @Override
    public ReservationEvenement updateReservation(ReservationEvenement reservation) {
        if (!reservationRepository.existsById(reservation.getId()))
            throw new RuntimeException("Réservation introuvable avec l'id: " + reservation.getId());
        return reservationRepository.save(reservation);
    }

    @Override
    public void deleteReservation(Long id) {
        if (!reservationRepository.existsById(id))
            throw new RuntimeException("Réservation introuvable avec l'id: " + id);
        reservationRepository.deleteById(id);
    }

    @Override
    public ReservationEvenement getReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable avec l'id: " + id));
    }

    @Override
    public List<ReservationEvenement> getAllReservations() {
        return reservationRepository.findAll();
    }

    @Override
    public ReservationEvenement reserverEvenement(Long userId, Long evenementId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec l'id: " + userId));

        Evenement evenement = evenementRepository.findById(evenementId)
                .orElseThrow(() -> new RuntimeException("Événement introuvable avec l'id: " + evenementId));

        if (evenement.getStatus() != EvenementStatus.PUBLISHED)
            throw new RuntimeException("Cet événement n'est pas ouvert aux réservations");

        if (evenement.getDateDebut().isBefore(LocalDateTime.now()))
            throw new RuntimeException("Cet événement est déjà passé");

        if (userAlreadyReserved(userId, evenementId))
            throw new RuntimeException("Vous avez déjà une réservation pour cet événement");

        ReservationEvenement reservation = new ReservationEvenement();
        reservation.setUser(user);
        reservation.setEvenement(evenement);
        reservation.setDateReservation(LocalDateTime.now());

        int activeReservations = evenementRepository.countActiveReservations(evenementId);

        if (activeReservations >= evenement.getNombreDePlaces()) {
            // ✅ Event is full — add to waitlist
            reservation.setStatut(ReservationStatus.PENDING);
            reservation.setEnAttente(true);
            reservation.setDateExpiration(null); // no expiry for waitlist
        } else {
            // ✅ Place available — normal reservation with expiry
            reservation.setStatut(ReservationStatus.PENDING);
            reservation.setEnAttente(false);
            reservation.setDateExpiration(LocalDateTime.now().plusMinutes(EXPIRY_MINUTES));
        }

        return reservationRepository.save(reservation);
    }

    @Override
    public ReservationEvenement annulerReservation(Long reservationId) {
        ReservationEvenement reservation = getReservationById(reservationId);

        if (reservation.getStatut() == ReservationStatus.CANCELLED)
            throw new RuntimeException("Cette réservation est déjà annulée");

        reservation.setStatut(ReservationStatus.CANCELLED);
        ReservationEvenement saved = reservationRepository.save(reservation);

        // ✅ If cancelled reservation was a confirmed spot, promote first waitlist entry
        if (!reservation.isEnAttente()) {
            promoteFromWaitlist(reservation.getEvenement().getId());
        }

        return saved;
    }

    @Override
    public ReservationEvenement confirmerReservation(Long reservationId) {
        ReservationEvenement reservation = getReservationById(reservationId);

        if (reservation.getStatut() == ReservationStatus.CANCELLED)
            throw new RuntimeException("Impossible de confirmer une réservation annulée");

        if (reservation.getStatut() == ReservationStatus.CONFIRMED)
            throw new RuntimeException("Cette réservation est déjà confirmée");

        if (reservation.isEnAttente())
            throw new RuntimeException("Cette réservation est en liste d'attente");

        reservation.setStatut(ReservationStatus.CONFIRMED);
        reservation.setDateExpiration(null); // confirmed — no more expiry
        return reservationRepository.save(reservation);
    }

    // ✅ Promote the first waitlist entry when a spot opens up
    private void promoteFromWaitlist(Long evenementId) {
        List<ReservationEvenement> waitlist =
                reservationRepository.findWaitlistByEvenement(evenementId);
        if (!waitlist.isEmpty()) {
            ReservationEvenement next = waitlist.get(0);
            next.setEnAttente(false);
            next.setDateExpiration(LocalDateTime.now().plusMinutes(EXPIRY_MINUTES));
            reservationRepository.save(next);
        }
    }

    // ✅ Called by scheduler — auto-cancel expired pending reservations
    @Override
    public void expireReservations() {
        List<ReservationEvenement> expired =
                reservationRepository.findExpiredPendingReservations(LocalDateTime.now());
        for (ReservationEvenement r : expired) {
            r.setStatut(ReservationStatus.CANCELLED);
            reservationRepository.save(r);
            promoteFromWaitlist(r.getEvenement().getId());
        }
    }

    @Override
    public List<ReservationEvenement> getReservationsByUser(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    @Override
    public List<ReservationEvenement> getReservationsByEvenement(Long evenementId) {
        return reservationRepository.findByEvenementId(evenementId);
    }

    @Override
    public List<ReservationEvenement> getReservationsByStatus(ReservationStatus statut) {
        return reservationRepository.findByStatut(statut);
    }

    @Override
    public boolean userAlreadyReserved(Long userId, Long evenementId) {
        return reservationRepository.existsByUserIdAndEvenementIdAndStatutNot(
                userId, evenementId, ReservationStatus.CANCELLED);
    }

    @Override
    public List<ReservationEvenement> getWaitlistByEvenement(Long evenementId) {
        return reservationRepository.findWaitlistByEvenement(evenementId);
    }

    @Override
    public List<ReservationEvenement> getUserWaitlist(Long userId) {
        return reservationRepository.findByUserIdAndEnAttenteTrue(userId);
    }
}