package org.example.rawabet.cinema.mappers;


import org.example.rawabet.cinema.dto.response.SalleResponse;
import org.example.rawabet.cinema.entities.SalleCinema;

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
