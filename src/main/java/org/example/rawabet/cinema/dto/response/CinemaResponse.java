package org.example.rawabet.cinema.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class CinemaResponse {

    private Long id;

    private String name;

    private String slug;

    private String address;

    private String city;

    private String country;

    private String phone;

    private String email;

    private Boolean isActive;

}