package org.example.rawabet.services.IService.cinema;



import org.example.rawabet.dto.cinema.request.CreateFilmRequest;
import org.example.rawabet.dto.cinema.response.FilmResponse;

import java.util.List;

public interface IFilmService {

    FilmResponse createFilm(CreateFilmRequest request);

    List<FilmResponse> getActiveFilms();

    FilmResponse getFilmById(Long id);

    void disableFilm(Long id);

}
