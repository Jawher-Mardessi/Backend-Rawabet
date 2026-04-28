package org.example.rawabet.cinema.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.rawabet.cinema.dto.request.CreateFilmRequest;
import org.example.rawabet.cinema.dto.response.FilmResponse;
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

    @PostMapping
    @PreAuthorize("hasAuthority('FILM_CREATE')")
    public FilmResponse createFilm(
            @Valid @RequestBody CreateFilmRequest request) {

        return filmService.createFilm(request);
    }

    @GetMapping
    public List<FilmResponse> getFilms() {

        return filmService.getActiveFilms();
    }

    @GetMapping("/names")
    public List<Map<String, Object>> getFilmNames() {
        return filmService.getActiveFilms().stream()
                .map(f -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", f.getId());
                    map.put("title", f.getTitle());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public FilmResponse getFilm(
            @PathVariable Long id) {

        return filmService.getFilmById(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FILM_DELETE')")
    public void disableFilm(@PathVariable Long id) {

        filmService.disableFilm(id);
    }
}
