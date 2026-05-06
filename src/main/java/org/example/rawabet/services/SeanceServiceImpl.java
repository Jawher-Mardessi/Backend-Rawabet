package org.example.rawabet.services;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.cinema.dto.response.FilmResponse;
import org.example.rawabet.cinema.dto.response.SalleResponse;
import org.example.rawabet.cinema.entities.Film;
import org.example.rawabet.cinema.entities.SalleCinema;
import org.example.rawabet.cinema.repositories.FilmRepository;
import org.example.rawabet.cinema.repositories.SalleCinemaRepository;
import org.example.rawabet.dto.seance.request.CreateSeanceRequest;
import org.example.rawabet.dto.seance.response.SeanceResponse;
import org.example.rawabet.entities.Seance;
import org.example.rawabet.events.SeanceCreatedEvent;
import org.example.rawabet.events.SeanceDeletedEvent;
import org.example.rawabet.repositories.SeanceRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeanceServiceImpl implements ISeanceService {

    private final SeanceRepository seanceRepository;
    private final FilmRepository filmRepository;
    private final SalleCinemaRepository salleCinemaRepository;
    private final ApplicationEventPublisher eventPublisher;

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm");

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

        eventPublisher.publishEvent(new SeanceCreatedEvent(
                this,
                seance.getId(),
                film.getTitle(),
                seance.getDateHeure().format(TIME_FORMATTER),
                film.getDurationMinutes() != null ? film.getDurationMinutes() : 0
        ));

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
        eventPublisher.publishEvent(new SeanceDeletedEvent(this, id));
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
        return seanceRepository.findAllWithFilmAndSalle()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private SeanceResponse mapToResponse(Seance seance) {
        SeanceResponse response = SeanceResponse.builder()
                .id(seance.getId())
                .dateHeure(seance.getDateHeure())
                .prixBase(seance.getPrixBase())
                .langue(seance.getLangue())
                .film(mapToFilmResponse(seance.getFilm()))
                .salleCinema(mapToSalleResponse(seance.getSalleCinema()))
                .build();

        if (seance.getFilm() != null) {
            response.setFilmTitle(seance.getFilm().getTitle());
        }
        if (seance.getSalleCinema() != null) {
            response.setSalleCinemaName(seance.getSalleCinema().getName());
        }

        return response;
    }

    private FilmResponse mapToFilmResponse(Film film) {
        if (film == null) return null;
        return FilmResponse.builder()
                .id(film.getId())
                .title(film.getTitle())
                .synopsis(film.getSynopsis())
                .durationMinutes(film.getDurationMinutes())
                .language(film.getLanguage())
                .genre(film.getGenre())
                .director(film.getDirector())
                .rating(film.getRating())
                .releaseDate(film.getReleaseDate())
                .posterUrl(film.getPosterUrl())
                .averageRating(film.getAverageRating())
                .totalReviews(film.getTotalReviews())
                .trailerUrl(film.getTrailerUrl())
                .build();
    }

    private SalleResponse mapToSalleResponse(SalleCinema salle) {
        if (salle == null) return null;
        return SalleResponse.builder()
                .id(salle.getId())
                .name(salle.getName())
                .hallType(salle.getHallType())
                .screenType(salle.getScreenType())
                .totalCapacity(salle.getTotalCapacity())
                .isActive(salle.getIsActive())
                .build();
    }
}
