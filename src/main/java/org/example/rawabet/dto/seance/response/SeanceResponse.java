package org.example.rawabet.dto.seance.response;

import lombok.Builder;
import lombok.Data;
import org.example.rawabet.cinema.dto.response.FilmResponse;
import org.example.rawabet.cinema.dto.response.SalleResponse;

import java.time.LocalDateTime;

@Data
@Builder
public class SeanceResponse {
    private Long id;
    private LocalDateTime dateHeure;
    private double prixBase;
    private String langue;
    private String filmTitle;
    private String salleCinemaName;
    private FilmResponse film;
    private SalleResponse salleCinema;
}
