package org.example.rawabet.services.ServiceImpl.cinema;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.cinema.request.CreateFilmRequest;
import org.example.rawabet.dto.cinema.response.FilmResponse;
import org.example.rawabet.entities.cinema.Film;
import org.example.rawabet.mappers.cinema.FilmMapper;
import org.example.rawabet.repositories.cinema.FilmRepository;
import org.example.rawabet.services.IService.cinema.IFilmService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class FilmServiceImpl implements IFilmService {

    private final FilmRepository filmRepository;

    @Override
    public FilmResponse createFilm(CreateFilmRequest request) {

        if(request.getImdbId() != null &&
                filmRepository.findByImdbId(request.getImdbId()).isPresent()){

            throw new RuntimeException("Film already exists");
        }

        Film film = new Film();

        film.setTitle(request.getTitle());

        film.setSynopsis(request.getSynopsis());

        film.setDurationMinutes(request.getDurationMinutes());

        film.setLanguage(request.getLanguage());

        film.setGenre(request.getGenre());

        film.setDirector(request.getDirector());

        film.setCastSummary(request.getCastSummary());

        film.setRating(request.getRating());

        film.setReleaseDate(request.getReleaseDate());

        film.setPosterUrl(request.getPosterUrl());

        film.setTrailerUrl(request.getTrailerUrl());

        film.setImdbId(request.getImdbId());

        film.setAverageRating(0f);

        film.setTotalReviews(0);

        film.setIsActive(true);

        return FilmMapper.toResponse(
                filmRepository.save(film)
        );

    }

    @Override
    public List<FilmResponse> getActiveFilms() {

        return filmRepository
                .findByIsActiveTrue()
                .stream()
                .map(FilmMapper::toResponse)
                .toList();

    }

    @Override
    public FilmResponse getFilmById(Long id) {

        Film film = filmRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Film not found")
                );

        return FilmMapper.toResponse(film);

    }

    @Override
    public void disableFilm(Long id) {

        Film film = filmRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Film not found")
                );

        film.setIsActive(false);

        filmRepository.save(film);

    }

}
