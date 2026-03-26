package org.example.rawabet.controllers;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.entities.Film;
import org.example.rawabet.services.IFilmService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor

public class FilmController {

    private final IFilmService filmService;

    // CREATE
    @PostMapping("/add")
    public Film addFilm(@RequestBody Film film) {
        return filmService.addFilm(film);
    }

    // UPDATE
    @PutMapping("/update")
    public Film updateFilm(@RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    // DELETE
    @DeleteMapping("/delete/{id}")
    public void deleteFilm(@PathVariable Long id) {
        filmService.deleteFilm(id);
    }

    // GET BY ID
    @GetMapping("/get/{id}")
    public Film getFilmById(@PathVariable Long id) {
        return filmService.getFilmById(id);
    }

    // GET ALL
    @GetMapping("/all")
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

}