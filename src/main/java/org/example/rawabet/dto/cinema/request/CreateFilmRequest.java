package org.example.rawabet.dto.cinema.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateFilmRequest {

    private String title;

    private String synopsis;

    private Integer durationMinutes;

    private String language;

    private String genre;

    private String director;

    private String castSummary;

    private String rating;

    private LocalDate releaseDate;

    private String posterUrl;

    private String trailerUrl;

    private String imdbId;

}
