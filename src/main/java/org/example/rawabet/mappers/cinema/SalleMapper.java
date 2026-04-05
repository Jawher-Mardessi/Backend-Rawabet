package org.example.rawabet.mappers.cinema;

import org.example.rawabet.dto.cinema.response.SalleResponse;
import org.example.rawabet.entities.cinema.SalleCinema;

public class SalleMapper {

    public static SalleResponse toResponse(SalleCinema salle){

        return SalleResponse.builder()
                .id(salle.getId())
                .name(salle.getName())
                .hallType(salle.getHallType())
                .screenType(salle.getScreenType())
                .totalCapacity(salle.getTotalCapacity())
                .isActive(salle.getIsActive())
                .build();

    }

}
