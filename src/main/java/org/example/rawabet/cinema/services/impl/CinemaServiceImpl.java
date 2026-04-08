package org.example.rawabet.cinema.services.impl;

import lombok.RequiredArgsConstructor;

import org.example.rawabet.cinema.dto.request.CreateCinemaRequest;
import org.example.rawabet.cinema.dto.response.CinemaResponse;
import org.example.rawabet.cinema.entities.Cinema;
import org.example.rawabet.cinema.mappers.CinemaMapper;
import org.example.rawabet.cinema.repositories.CinemaRepository;
import org.example.rawabet.cinema.services.interfaces.ICinemaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class CinemaServiceImpl implements ICinemaService {

    private final CinemaRepository cinemaRepository;

    @Override
    public CinemaResponse createCinema(CreateCinemaRequest request) {

        Cinema cinema = new Cinema();

        cinema.setName(request.getName());

        cinema.setAddress(request.getAddress());

        cinema.setCity(request.getCity());

        cinema.setCountry(request.getCountry());

        cinema.setPhone(request.getPhone());

        cinema.setEmail(request.getEmail());

        cinema.setOpeningHours(request.getOpeningHours());

        cinema.setSlug(
                request.getName()
                        .toLowerCase()
                        .replace(" ","-")
        );

        cinema.setIsActive(true);

        return CinemaMapper.toResponse(
                cinemaRepository.save(cinema)
        );

    }

    @Override
    public List<CinemaResponse> getActiveCinemas() {

        return cinemaRepository
                .findByIsActiveTrue()
                .stream()
                .map(CinemaMapper::toResponse)
                .toList();

    }

    @Override
    public CinemaResponse getCinemaById(Long id) {

        Cinema cinema = cinemaRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Cinema not found")
                );

        return CinemaMapper.toResponse(cinema);

    }

    @Override
    public void disableCinema(Long id) {

        Cinema cinema = cinemaRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Cinema not found")
                );

        cinema.setIsActive(false);

        cinemaRepository.save(cinema);

    }

}