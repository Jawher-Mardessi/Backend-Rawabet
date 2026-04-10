package org.example.rawabet.cinema.services.interfaces;



import org.example.rawabet.cinema.dto.request.CreateCinemaRequest;
import org.example.rawabet.cinema.dto.response.CinemaResponse;

import java.util.List;

public interface ICinemaService {

    CinemaResponse createCinema(CreateCinemaRequest request);

    List<CinemaResponse> getActiveCinemas();

    CinemaResponse getCinemaById(Long id);

    void disableCinema(Long id);

}
