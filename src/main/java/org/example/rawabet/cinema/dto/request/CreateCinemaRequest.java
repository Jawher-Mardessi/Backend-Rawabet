package org.example.rawabet.cinema.dto.request;

import lombok.Data;

@Data
public class CreateCinemaRequest {

    private String name;

    private String address;

    private String city;

    private String country;

    private String phone;

    private String email;

    private String openingHours;

}