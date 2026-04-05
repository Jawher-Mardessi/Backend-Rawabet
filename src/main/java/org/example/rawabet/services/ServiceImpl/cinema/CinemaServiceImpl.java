package org.example.rawabet.services.ServiceImpl.cinema;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.cinema.request.CreateCinemaRequest;
import org.example.rawabet.dto.cinema.response.CinemaResponse;
import org.example.rawabet.entities.cinema.Cinema;
import org.example.rawabet.mappers.cinema.CinemaMapper;
import org.example.rawabet.repositories.cinema.CinemaRepository;
import org.example.rawabet.services.IService.cinema.ICinemaService;
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