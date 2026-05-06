package org.example.rawabet.cinema.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.rawabet.cinema.dto.request.CreateFilmRequest;
import org.example.rawabet.cinema.dto.response.FilmResponse;
import org.example.rawabet.cinema.services.impl.FilmRoiService;
import org.example.rawabet.cinema.services.interfaces.IFilmService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/films")
@RequiredArgsConstructor
public class FilmController {

    private final IFilmService filmService;
    private final FilmRoiService filmRoiService;

    @PostMapping
    @PreAuthorize("hasAuthority('FILM_CREATE')")
    public FilmResponse createFilm(@Valid @RequestBody CreateFilmRequest request) {
        return filmService.createFilm(request);
    }

    @GetMapping
    public List<FilmResponse> getFilms() {
        return filmService.getActiveFilms();
    }

    @GetMapping("/names")
    public List<Map<String, Object>> getFilmNames() {
        return filmService.getActiveFilms().stream()
                .map(film -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", film.getId());
                    map.put("title", film.getTitle());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public FilmResponse getFilm(@PathVariable Long id) {
        return filmService.getFilmById(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FILM_DELETE')")
    public void disableFilm(@PathVariable Long id) {
        filmService.disableFilm(id);
    }

    @PostMapping("/roi-predict")
    public Map<String, Object> predictProgramming(@RequestBody Map<String, Object> payload) {

        String title = (String) payload.get("title");
        double budget = ((Number) payload.get("budget")).doubleValue();
        double runtime = ((Number) payload.get("runtime")).doubleValue();
        int releaseYear = ((Number) payload.get("release_year")).intValue();
        int releaseMonth = ((Number) payload.get("release_month")).intValue();
        String releaseDate = (String) payload.getOrDefault("release_date", null);
        String language = (String) payload.getOrDefault("language", "en");
        String overview = (String) payload.getOrDefault("overview", "");

        @SuppressWarnings("unchecked")
        List<String> genres = (List<String>) payload.getOrDefault("genres", List.of());

        FilmRoiService.RoiPredictionResult result = filmRoiService.predict(
                title,
                budget,
                runtime,
                releaseYear,
                releaseMonth,
                releaseDate,
                language,
                genres,
                overview
        );

        if (result == null) {
            return Map.of(
                    "ai_score", 0.0,
                    "temporal_score", 0.0,
                    "final_score", 0.0,
                    "temporal_label", "Service indisponible",
                    "temporal_status", "unknown",
                    "recommendation", "Service indisponible",
                    "recommendation_level", "no",
                    "label", "Indisponible"
            );
        }

        return Map.of(
                "ai_score", result.getAiScore(),
                "temporal_score", result.getTemporalScore(),
                "final_score", result.getFinalScore(),
                "temporal_label", result.getTemporalLabel(),
                "temporal_status", result.getTemporalStatus(),
                "weeks_since_release", result.getWeeksSinceRelease(),
                "recommendation", result.getRecommendation(),
                "recommendation_level", result.getRecommendationLevel(),
                "label", result.getLabel()
        );
    }
}
