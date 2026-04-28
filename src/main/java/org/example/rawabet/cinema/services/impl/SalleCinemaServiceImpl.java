package org.example.rawabet.cinema.services.impl;

import lombok.RequiredArgsConstructor;

import org.example.rawabet.cinema.dto.request.CreateSalleRequest;
import org.example.rawabet.cinema.dto.response.SalleResponse;
import org.example.rawabet.cinema.entities.Cinema;
import org.example.rawabet.cinema.entities.SalleCinema;
import org.example.rawabet.cinema.exceptions.ResourceNotFoundException;
import org.example.rawabet.cinema.mappers.SalleMapper;
import org.example.rawabet.cinema.repositories.CinemaRepository;
import org.example.rawabet.cinema.repositories.SalleCinemaRepository;
import org.example.rawabet.cinema.repositories.SeatRepository;
import org.example.rawabet.cinema.services.interfaces.ISalleCinemaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor

public class SalleCinemaServiceImpl implements ISalleCinemaService {

    private final SalleCinemaRepository salleRepository;

    private final CinemaRepository cinemaRepository;

    private final SeatRepository seatRepository;

    @Override
    @Transactional
    public SalleResponse createSalle(CreateSalleRequest request) {

        Cinema cinema = cinemaRepository
                .findById(request.getCinemaId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cinéma introuvable avec l'id : " + request.getCinemaId())
                );

        SalleCinema salle = SalleCinema.builder()
                .name(request.getName())
                .hallType(request.getHallType())
                .screenType(request.getScreenType())
                .cinema(cinema)
                .totalCapacity(0)
                .isActive(true)
                .build();

        return SalleMapper.toResponse(
                salleRepository.save(salle)
        );

    }

    @Override
    public List<SalleResponse> getCinemaSalles(Long cinemaId) {

        return salleRepository
                .findByCinemaIdAndIsActiveTrue(cinemaId)
                .stream()
                .map(SalleMapper::toResponse)
                .toList();

    }

    @Override
    public SalleResponse getSalleById(Long id) {

        SalleCinema salle = salleRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Salle introuvable avec l'id : " + id)
                );

        return SalleMapper.toResponse(salle);

    }

    @Override
    @Transactional
    public void disableSalle(Long id) {

        SalleCinema salle = salleRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Salle introuvable avec l'id : " + id)
                );

        // Désactivation en cascade des sièges de la salle
        seatRepository.disableAllBySalleId(id);

        salle.setIsActive(false);

        salleRepository.save(salle);

    }

}
