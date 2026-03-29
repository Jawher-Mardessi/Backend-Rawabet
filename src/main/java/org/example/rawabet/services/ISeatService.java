package org.example.rawabet.services;

import org.example.rawabet.entities.Seat;

import java.util.List;

public interface ISeatService {

    Seat addSeat(Seat seat);

    Seat updateSeat(Seat seat);

    void deleteSeat(Long id);

    Seat getSeatById(Long id);

    List<Seat> getAllSeats();

}