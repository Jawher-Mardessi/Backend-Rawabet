package org.example.rawabet.cinema.services.impl;

import lombok.RequiredArgsConstructor;

import org.example.rawabet.cinema.dto.request.CreateFilmRequest;
import org.example.rawabet.cinema.dto.response.FilmResponse;
import org.example.rawabet.cinema.entities.Film;
import org.example.rawabet.cinema.exceptions.ResourceNotFoundException;
import org.example.rawabet.cinema.mappers.FilmMapper;
import org.example.rawabet.cinema.repositories.FilmRepository;
import org.example.rawabet.cinema.services.interfaces.IFilmService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements IFilmService {

    private final FilmRepository filmRepository;
    private final FilmRoiService filmRoiService;

    @Override
    @Transactional
    public FilmResponse createFilm(CreateFilmRequest request) {

        if (request.getImdbId() != null &&
                filmRepository.findByImdbId(request.getImdbId()).isPresent()) {
            throw new IllegalArgumentException(
                    "Un film avec l'imdbId '" + request.getImdbId() + "' existe déjà"
            );
        }

        Film film = Film.builder()
                .title(request.getTitle())
                .synopsis(request.getSynopsis())
                .durationMinutes(request.getDurationMinutes())
                .language(request.getLanguage())
                .genre(request.getGenre())
                .director(request.getDirector())
                .castSummary(request.getCastSummary())
                .rating(request.getRating())
                .releaseDate(request.getReleaseDate())
                .posterUrl(request.getPosterUrl())
                .trailerUrl(request.getTrailerUrl())
                .imdbId(request.getImdbId())
                .averageRating(0f)
                .totalReviews(0)
                .isActive(true)
                .build();

        // ── Prédiction fenêtre d'exploitation (non bloquante) ────
        if (request.getBudget() != null && request.getBudget() > 0
                && request.getReleaseDate() != null
                && request.getDurationMinutes() != null) {

            List<String> genres = request.getGenre() != null
                    ? Arrays.asList(request.getGenre().split(",\\s*"))
                    : List.of();

            String releaseDateStr = request.getReleaseDate().toString();

            FilmRoiService.RoiPredictionResult result = filmRoiService.predict(
                    request.getTitle(),
                    request.getBudget(),
                    request.getDurationMinutes(),
                    request.getReleaseDate().getYear(),
                    request.getReleaseDate().getMonthValue(),
                    releaseDateStr,
                    request.getLanguage() != null ? request.getLanguage() : "en",
                    genres,
                    request.getSynopsis() != null ? request.getSynopsis() : ""
            );

            if (result != null) {
                // profitable = true si recommandé pour programmation
                film.setProfitable(
                        result.getRecommendationLevel().equals("strong_yes") ||
                                result.getRecommendationLevel().equals("yes")
                );
                film.setRoiConfidence(result.getFinalScore());
                film.setRoiLabel(result.getLabel());
            }
        }

        return FilmMapper.toResponse(filmRepository.save(film));
    }

    @Override
    public List<FilmResponse> getActiveFilms() {
        return filmRepository.findByIsActiveTrue()
                .stream().map(FilmMapper::toResponse).toList();
    }

    @Override
    public FilmResponse getFilmById(Long id) {
        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Film introuvable avec l'id : " + id));
        return FilmMapper.toResponse(film);
    }

    @Override
    @Transactional
    public void disableFilm(Long id) {
        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Film introuvable avec l'id : " + id));
        film.setIsActive(false);
        filmRepository.save(film);
    }
}