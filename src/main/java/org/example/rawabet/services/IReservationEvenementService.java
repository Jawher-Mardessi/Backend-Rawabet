package org.example.rawabet.services;

import org.example.rawabet.entities.ReservationEvenement;
import org.example.rawabet.enums.ReservationStatus;
import java.util.List;

public interface IReservationEvenementService {
    ReservationEvenement addReservation(ReservationEvenement reservation);
    ReservationEvenement updateReservation(ReservationEvenement reservation);
    void deleteReservation(Long id);
    ReservationEvenement getReservationById(Long id);
    List<ReservationEvenement> getAllReservations();
    ReservationEvenement reserverEvenement(Long userId, Long evenementId);
    ReservationEvenement annulerReservation(Long reservationId);
    ReservationEvenement confirmerReservation(Long reservationId);
    List<ReservationEvenement> getReservationsByUser(Long userId);
    List<ReservationEvenement> getReservationsByEvenement(Long evenementId);
    List<ReservationEvenement> getReservationsByStatus(ReservationStatus statut);
    boolean userAlreadyReserved(Long userId, Long evenementId);
    void expireReservations();                                        // ✅ new - called by scheduler
    List<ReservationEvenement> getWaitlistByEvenement(Long evenementId);  // ✅ new
    List<ReservationEvenement> getUserWaitlist(Long userId);              // ✅ new
}