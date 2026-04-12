package org.example.rawabet.cinema.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCinemaRequest {

    @NotBlank(message = "Le nom du cinéma est obligatoire")
    private String name;

    @NotBlank(message = "L'adresse est obligatoire")
    private String address;

    @NotBlank(message = "La ville est obligatoire")
    private String city;

    @NotBlank(message = "Le pays est obligatoire")
    private String country;

    private String phone;

    @Email(message = "Format d'email invalide")
    private String email;

    private String openingHours;

}