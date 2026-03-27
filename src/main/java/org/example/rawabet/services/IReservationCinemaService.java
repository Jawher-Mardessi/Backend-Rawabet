package org.example.rawabet.services;

import org.example.rawabet.entities.ReservationCinema;

import java.util.List;

public interface IReservationCinemaService {

    ReservationCinema reserverAvecSeats(
            Long userId,
            Long seanceId,
            List<Long> seatIds);

    List<ReservationCinema> getAllReservations();

    ReservationCinema getReservationById(Long id);

}