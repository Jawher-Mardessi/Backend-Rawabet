package org.example.rawabet.cinema.mappers;


import org.example.rawabet.cinema.dto.response.CinemaResponse;
import org.example.rawabet.cinema.entities.Cinema;

public class CinemaMapper {

    public static CinemaResponse toResponse(Cinema cinema) {
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
                .latitude(cinema.getLatitude())
                .longitude(cinema.getLongitude())
                .timezone(cinema.getTimezone())
                .build();
    }

}
