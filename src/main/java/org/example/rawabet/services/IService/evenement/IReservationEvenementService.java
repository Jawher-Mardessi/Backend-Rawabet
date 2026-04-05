package org.example.rawabet.services.IService.evenement;

import org.example.rawabet.entities.ReservationEvenement;

import java.util.List;

public interface IReservationEvenementService {

    ReservationEvenement addReservation(ReservationEvenement reservation);

    ReservationEvenement updateReservation(ReservationEvenement reservation);

    void deleteReservation(Long id);

    ReservationEvenement getReservationById(Long id);

    List<ReservationEvenement> getAllReservations();
}