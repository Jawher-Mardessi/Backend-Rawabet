package org.example.rawabet.cinema.controllers;


import lombok.RequiredArgsConstructor;

import org.example.rawabet.cinema.dto.response.CinemaResponse;
import org.example.rawabet.cinema.services.interfaces.ICinemaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cinemas")

@RequiredArgsConstructor

public class PublicCinemaController {

    private final ICinemaService cinemaService;

    @GetMapping
    public List<CinemaResponse> getCinemas(){

        return cinemaService.getActiveCinemas();

    }

    @GetMapping("/{id}")
    public CinemaResponse getCinema(
            @PathVariable Long id){

        return cinemaService.getCinemaById(id);

    }

}
