package org.example.rawabet.cinema.services.interfaces;

import org.example.rawabet.cinema.dto.request.ConfigureHallRequest;
import org.example.rawabet.cinema.dto.response.SeatResponse;
import org.example.rawabet.cinema.dto.response.SeatRowResponse;

import java.util.List;

public interface ISeatService {

    void configureHall(ConfigureHallRequest request);

    List<SeatResponse> getSeatsBySalle(Long salleId);

    List<SeatRowResponse> getRowsBySalle(Long salleId); // ← NOUVEAU

    void disableSeat(Long seatId);
    List<SeatResponse> getSeatResponsesBySeance(Long seanceId);
}