package org.example.rawabet.cinema.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.rawabet.cinema.enums.HallType;
import org.example.rawabet.cinema.enums.ScreenType;

@Data
public class CreateSalleRequest {

    @NotNull(message = "L'identifiant du cinéma est obligatoire")
    private Long cinemaId;

    @NotBlank(message = "Le nom de la salle est obligatoire")
    private String name;

    @NotNull(message = "Le type de salle est obligatoire")
    private HallType hallType;

    @NotNull(message = "Le type d'écran est obligatoire")
    private ScreenType screenType;

}