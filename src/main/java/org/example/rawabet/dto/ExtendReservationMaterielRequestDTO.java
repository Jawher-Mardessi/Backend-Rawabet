package org.example.rawabet.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class ExtendReservationMaterielRequestDTO {

    @NotNull(message = "La nouvelle date de fin est obligatoire")
    private LocalDateTime nouvelleDataFin;

    public ExtendReservationMaterielRequestDTO() {}

    public LocalDateTime getNouvelleDataFin() { return nouvelleDataFin; }
    public void setNouvelleDataFin(LocalDateTime nouvelleDataFin) { this.nouvelleDataFin = nouvelleDataFin; }
}