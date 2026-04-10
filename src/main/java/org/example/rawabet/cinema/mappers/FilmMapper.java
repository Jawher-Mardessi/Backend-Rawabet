package org.example.rawabet.cinema.mappers;


import org.example.rawabet.cinema.dto.response.FilmResponse;
import org.example.rawabet.cinema.entities.Film;

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