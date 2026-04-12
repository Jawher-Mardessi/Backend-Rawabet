package org.example.rawabet.cinema.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.example.rawabet.cinema.dto.request.CreateCinemaRequest;
import org.example.rawabet.cinema.dto.response.CinemaResponse;
import org.example.rawabet.cinema.services.interfaces.ICinemaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/cinemas")

@RequiredArgsConstructor

public class AdminCinemaController {

    private final ICinemaService cinemaService;

    @PostMapping
    @PreAuthorize("hasAuthority('CINEMA_CREATE')")
    public CinemaResponse createCinema(
            @Valid @RequestBody CreateCinemaRequest request){

        return cinemaService.createCinema(request);

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CINEMA_DELETE')")
    public void disableCinema(@PathVariable Long id){

        cinemaService.disableCinema(id);

    }

}