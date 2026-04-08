package org.example.rawabet.cinema.services.interfaces;


import org.example.rawabet.cinema.dto.request.CreateSalleRequest;
import org.example.rawabet.cinema.dto.response.SalleResponse;

import java.util.List;

public interface ISalleCinemaService {

    SalleResponse createSalle(CreateSalleRequest request);

    List<SalleResponse> getCinemaSalles(Long cinemaId);

    SalleResponse getSalleById(Long id);

    void disableSalle(Long id);

}