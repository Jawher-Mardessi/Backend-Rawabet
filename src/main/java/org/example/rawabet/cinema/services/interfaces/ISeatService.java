package org.example.rawabet.cinema.services.interfaces;


import org.example.rawabet.cinema.dto.request.ConfigureHallRequest;
import org.example.rawabet.cinema.dto.response.SeatResponse;

import java.util.List;

public interface ISeatService {

    void configureHall(ConfigureHallRequest request);

    List<SeatResponse> getSeatsBySalle(Long salleId);

    void disableSeat(Long seatId);

}
