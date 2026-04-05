package org.example.rawabet.services.IService.cinema;

import org.example.rawabet.dto.cinema.request.CreateCinemaRequest;
import org.example.rawabet.dto.cinema.response.CinemaResponse;

import java.util.List;

public interface ICinemaService {

    CinemaResponse createCinema(CreateCinemaRequest request);

    List<CinemaResponse> getActiveCinemas();

    CinemaResponse getCinemaById(Long id);

    void disableCinema(Long id);

}
