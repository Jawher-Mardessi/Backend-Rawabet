package org.example.rawabet.services;
import org.example.rawabet.entities.Film;
import java.util.List;

public interface IFilmService {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(Long id);

    Film getFilmById(Long id);

    List<Film> getAllFilms();
}