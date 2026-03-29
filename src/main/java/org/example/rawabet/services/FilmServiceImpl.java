package org.example.rawabet.services;

import org.example.rawabet.entities.Film;
import org.example.rawabet.repositories.FilmRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class FilmServiceImpl implements IFilmService {

    private final FilmRepository filmRepository;

    public FilmServiceImpl(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    @Override
    public Film addFilm(Film film) {
        return filmRepository.save(film);
    }

    @Override
    public Film updateFilm(Film film) {
        return filmRepository.save(film);
    }

    @Override
    public void deleteFilm(Long id) {
        filmRepository.deleteById(id);
    }

    @Override
    public Film getFilmById(Long id) {
        return filmRepository.findById(id).orElse(null);
    }

    @Override
    public List<Film> getAllFilms() {
        return filmRepository.findAll();
    }
}