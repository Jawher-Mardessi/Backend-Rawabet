package org.example.rawabet.mappers.cinema;

import org.example.rawabet.dto.cinema.response.CinemaResponse;
import org.example.rawabet.entities.cinema.Cinema;

public class CinemaMapper {

    public static CinemaResponse toResponse(Cinema cinema){

        return CinemaResponse.builder()
                .id(cinema.getId())
                .name(cinema.getName())
                .slug(cinema.getSlug())
                .address(cinema.getAddress())
                .city(cinema.getCity())
                .country(cinema.getCountry())
                .phone(cinema.getPhone())
                .email(cinema.getEmail())
                .isActive(cinema.getIsActive())
                .build();

    }

}
