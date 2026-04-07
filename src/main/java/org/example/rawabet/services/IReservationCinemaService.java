package org.example.rawabet.services;

import org.example.rawabet.dto.reservationCinema.request.CreateReservationCinemaRequest;
import org.example.rawabet.dto.reservationCinema.response.ReservationCinemaResponse;
import org.example.rawabet.entities.ReservationCinema;

import java.util.List;

public interface IReservationCinemaService {

    ReservationCinema addReservation(ReservationCinema reservation);

    ReservationCinema updateReservation(ReservationCinema reservation);

    void deleteReservation(Long id);

    ReservationCinema getReservationById(Long id);

    List<ReservationCinema> getAllReservations();

    ReservationCinemaResponse reserver(CreateReservationCinemaRequest request);
}