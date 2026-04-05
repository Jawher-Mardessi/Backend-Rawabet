package org.example.rawabet.dto.cinema.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder

public class FilmResponse {

    private Long id;

    private String title;

    private String synopsis;

    private Integer durationMinutes;

    private String language;

    private String genre;

    private String director;

    private String rating;

    private LocalDate releaseDate;

    private String posterUrl;

    private Float averageRating;

    private Integer totalReviews;

}