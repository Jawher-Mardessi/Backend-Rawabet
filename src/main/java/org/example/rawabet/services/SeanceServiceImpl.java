package org.example.rawabet.services;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.seance.request.CreateSeanceRequest;
import org.example.rawabet.dto.seance.response.SeanceResponse;
import org.example.rawabet.entities.Film;
import org.example.rawabet.entities.SalleCinema;
import org.example.rawabet.entities.Seance;
import org.example.rawabet.repositories.FilmRepository;
import org.example.rawabet.repositories.SalleCinemaRepository;
import org.example.rawabet.repositories.SeanceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeanceServiceImpl implements ISeanceService {

    private final SeanceRepository seanceRepository;
    private final FilmRepository filmRepository;
    private final SalleCinemaRepository salleCinemaRepository;

    @Override
    public SeanceResponse addSeance(CreateSeanceRequest request) {
        Film film = filmRepository.findById(request.getFilmId())
                .orElseThrow(() -> new RuntimeException("Film not found"));

        SalleCinema salleCinema = salleCinemaRepository.findById(request.getSalleCinemaId())
                .orElseThrow(() -> new RuntimeException("SalleCinema not found"));

        Seance seance = new Seance();
        seance.setDateHeure(LocalDateTime.parse(request.getDateHeure()));
        seance.setPrixBase(request.getPrixBase());
        seance.setLangue(request.getLangue());
        seance.setFilm(film);
        seance.setSalleCinema(salleCinema);

        seance = seanceRepository.save(seance);

        return mapToResponse(seance);
    }

    @Override
    public SeanceResponse updateSeance(Long id, CreateSeanceRequest request) {
        Seance seance = seanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seance not found"));

        Film film = filmRepository.findById(request.getFilmId())
                .orElseThrow(() -> new RuntimeException("Film not found"));

        SalleCinema salleCinema = salleCinemaRepository.findById(request.getSalleCinemaId())
                .orElseThrow(() -> new RuntimeException("SalleCinema not found"));

        seance.setDateHeure(LocalDateTime.parse(request.getDateHeure()));
        seance.setPrixBase(request.getPrixBase());
        seance.setLangue(request.getLangue());
        seance.setFilm(film);
        seance.setSalleCinema(salleCinema);

        seance = seanceRepository.save(seance);

        return mapToResponse(seance);
    }

    @Override
    public void deleteSeance(Long id) {
        seanceRepository.deleteById(id);
    }

    @Override
    public SeanceResponse getSeanceById(Long id) {
        return seanceRepository.findById(id)
                .map(this::mapToResponse)
                .orElse(null);
    }

    @Override
    public List<SeanceResponse> getAllSeances() {
        return seanceRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private SeanceResponse mapToResponse(Seance seance) {
        return SeanceResponse.builder()
                .id(seance.getId())
                .dateHeure(seance.getDateHeure().toString())
                .prixBase(seance.getPrixBase())
                .langue(seance.getLangue())
                .filmId(seance.getFilm() != null ? seance.getFilm().getId() : null)
                .salleCinemaId(seance.getSalleCinema() != null ? seance.getSalleCinema().getId() : null)
                .build();
    }
}