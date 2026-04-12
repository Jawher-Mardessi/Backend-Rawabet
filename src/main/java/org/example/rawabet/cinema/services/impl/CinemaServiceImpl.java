package org.example.rawabet.cinema.services.impl;

import lombok.RequiredArgsConstructor;

import org.example.rawabet.cinema.dto.request.CreateCinemaRequest;
import org.example.rawabet.cinema.dto.response.CinemaResponse;
import org.example.rawabet.cinema.entities.Cinema;
import org.example.rawabet.cinema.exceptions.ResourceNotFoundException;
import org.example.rawabet.cinema.mappers.CinemaMapper;
import org.example.rawabet.cinema.repositories.CinemaRepository;
import org.example.rawabet.cinema.services.interfaces.ICinemaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor

public class CinemaServiceImpl implements ICinemaService {

    private final CinemaRepository cinemaRepository;

    @Override
    @Transactional
    public CinemaResponse createCinema(CreateCinemaRequest request) {

        String slug = request.getName()
                .toLowerCase()
                .trim()
                .replaceAll("\\s+", "-")
                .replaceAll("[^a-z0-9\\-]", "");

        // Vérification unicité du slug avant save
        if (cinemaRepository.findBySlug(slug).isPresent()) {
            throw new IllegalArgumentException(
                    "Un cinéma avec le slug '" + slug + "' existe déjà"
            );
        }

        Cinema cinema = Cinema.builder()
                .name(request.getName())
                .slug(slug)
                .address(request.getAddress())
                .city(request.getCity())
                .country(request.getCountry())
                .phone(request.getPhone())
                .email(request.getEmail())
                .openingHours(request.getOpeningHours())
                .isActive(true)
                .build();

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
                        new ResourceNotFoundException("Cinéma introuvable avec l'id : " + id)
                );

        return CinemaMapper.toResponse(cinema);

    }

    @Override
    @Transactional
    public void disableCinema(Long id) {

        Cinema cinema = cinemaRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cinéma introuvable avec l'id : " + id)
                );

        cinema.setIsActive(false);

        cinemaRepository.save(cinema);

    }

}