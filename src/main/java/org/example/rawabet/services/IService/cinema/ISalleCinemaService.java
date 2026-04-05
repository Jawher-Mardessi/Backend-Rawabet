package org.example.rawabet.services.IService.cinema;

import org.example.rawabet.dto.cinema.request.CreateSalleRequest;
import org.example.rawabet.dto.cinema.response.SalleResponse;

import java.util.List;

public interface ISalleCinemaService {

    SalleResponse createSalle(CreateSalleRequest request);

    List<SalleResponse> getCinemaSalles(Long cinemaId);

    SalleResponse getSalleById(Long id);

    void disableSalle(Long id);

}