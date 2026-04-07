package org.example.rawabet.services;

import org.example.rawabet.dto.seance.request.CreateSeanceRequest;
import org.example.rawabet.dto.seance.response.SeanceResponse;

import java.util.List;

public interface ISeanceService {

    SeanceResponse addSeance(CreateSeanceRequest request);

    SeanceResponse updateSeance(Long id, CreateSeanceRequest request);

    void deleteSeance(Long id);

    SeanceResponse getSeanceById(Long id);

    List<SeanceResponse> getAllSeances();
}