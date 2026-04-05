package org.example.rawabet.mappers.cinema;

import org.example.rawabet.dto.cinema.response.FilmResponse;
import org.example.rawabet.entities.cinema.Film;

public class FilmMapper {

    public static FilmResponse toResponse(Film film){

        return FilmResponse.builder()
                .id(film.getId())
                .title(film.getTitle())
                .synopsis(film.getSynopsis())
                .durationMinutes(film.getDurationMinutes())
                .language(film.getLanguage())
                .genre(film.getGenre())
                .director(film.getDirector())
                .rating(film.getRating())
                .releaseDate(film.getReleaseDate())
                .posterUrl(film.getPosterUrl())
                .averageRating(film.getAverageRating())
                .totalReviews(film.getTotalReviews())
                .build();

    }

}