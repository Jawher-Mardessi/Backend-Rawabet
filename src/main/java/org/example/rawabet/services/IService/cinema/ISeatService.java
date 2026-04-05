package org.example.rawabet.services.IService.cinema;

import org.example.rawabet.dto.cinema.request.ConfigureHallRequest;
import org.example.rawabet.dto.cinema.response.SeatResponse;

import java.util.List;

public interface ISeatService {

    void configureHall(ConfigureHallRequest request);

    List<SeatResponse> getSeatsBySalle(Long salleId);

    void disableSeat(Long seatId);

}
