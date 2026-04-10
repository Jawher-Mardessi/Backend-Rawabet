package org.example.rawabet.cinema.controllers;


import lombok.RequiredArgsConstructor;

import org.example.rawabet.cinema.dto.request.CreateFilmRequest;
import org.example.rawabet.cinema.dto.response.FilmResponse;
import org.example.rawabet.cinema.services.interfaces.IFilmService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/films")

@RequiredArgsConstructor

public class FilmController {

    private final IFilmService filmService;

    @PostMapping
    @PreAuthorize("hasAuthority('FILM_CREATE')")
    public FilmResponse createFilm(
            @RequestBody CreateFilmRequest request){

        return filmService.createFilm(request);

    }

    @GetMapping
    public List<FilmResponse> getFilms(){

        return filmService.getActiveFilms();

    }

    @GetMapping("/{id}")
    public FilmResponse getFilm(
            @PathVariable Long id){

        return filmService.getFilmById(id);

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FILM_DELETE')")
    public void disableFilm(@PathVariable Long id){

        filmService.disableFilm(id);

    }

}
