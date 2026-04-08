package org.example.rawabet.cinema.services.interfaces;




import org.example.rawabet.cinema.dto.request.CreateFilmRequest;
import org.example.rawabet.cinema.dto.response.FilmResponse;

import java.util.List;

public interface IFilmService {

    FilmResponse createFilm(CreateFilmRequest request);

    List<FilmResponse> getActiveFilms();

    FilmResponse getFilmById(Long id);

    void disableFilm(Long id);

}
