package org.example.rawabet.services;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.entities.Film;
import org.example.rawabet.repositories.FilmRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class FilmServiceImpl implements IFilmService {

    private final FilmRepository filmRepository;

    // CREATE
    @Override
    public Film addFilm(Film film) {
        return filmRepository.save(film);
    }

    // UPDATE
    @Override
    public Film updateFilm(Film film) {
        return filmRepository.save(film);
    }

    // DELETE
    @Override
    public void deleteFilm(Long id) {

        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Film not found"));

        filmRepository.delete(film);
    }

    // GET BY ID
    @Override
    public Film getFilmById(Long id) {
        return filmRepository.findById(id)
                .orElse(null);
    }

    // GET ALL
    @Override
    public List<Film> getAllFilms() {
        return filmRepository.findAll();
    }

}