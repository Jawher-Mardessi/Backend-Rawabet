package org.example.rawabet.services.ServiceImpl.cinema;


import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.cinema.request.CreateSalleRequest;
import org.example.rawabet.dto.cinema.response.SalleResponse;
import org.example.rawabet.entities.cinema.Cinema;
import org.example.rawabet.entities.cinema.SalleCinema;
import org.example.rawabet.mappers.cinema.SalleMapper;
import org.example.rawabet.repositories.cinema.CinemaRepository;
import org.example.rawabet.repositories.cinema.SalleCinemaRepository;
import org.example.rawabet.services.IService.cinema.ISalleCinemaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class SalleCinemaServiceImpl implements ISalleCinemaService {

    private final SalleCinemaRepository salleRepository;

    private final CinemaRepository cinemaRepository;

    @Override
    public SalleResponse createSalle(CreateSalleRequest request) {

        Cinema cinema = cinemaRepository
                .findById(request.getCinemaId())
                .orElseThrow(() ->
                        new RuntimeException("Cinema not found")
                );

        SalleCinema salle = new SalleCinema();

        salle.setName(request.getName());

        salle.setHallType(request.getHallType());

        salle.setScreenType(request.getScreenType());

        salle.setCinema(cinema);

        salle.setTotalCapacity(0);

        salle.setIsActive(true);

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
                        new RuntimeException("Salle not found")
                );

        return SalleMapper.toResponse(salle);

    }

    @Override
    public void disableSalle(Long id) {

        SalleCinema salle = salleRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Salle not found")
                );

        salle.setIsActive(false);

        salleRepository.save(salle);

    }

}
