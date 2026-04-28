package org.example.rawabet.cinema.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.example.rawabet.cinema.dto.request.CreateSalleRequest;
import org.example.rawabet.cinema.dto.response.SalleResponse;
import org.example.rawabet.cinema.services.interfaces.ISalleCinemaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salles-cinema")

@RequiredArgsConstructor

public class SalleCinemaController {

    private final ISalleCinemaService salleService;

    @PostMapping
    @PreAuthorize("hasAuthority('CINEMA_UPDATE')")
    public SalleResponse createSalle(
            @Valid @RequestBody CreateSalleRequest request){

        return salleService.createSalle(request);

    }

    @GetMapping("/cinema/{cinemaId}")
    public List<SalleResponse> getCinemaSalles(
            @PathVariable Long cinemaId){

        return salleService.getCinemaSalles(cinemaId);

    }

    @GetMapping("/{id}")
    public SalleResponse getSalle(
            @PathVariable Long id){

        return salleService.getSalleById(id);

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CINEMA_UPDATE')")
    public void disableSalle(@PathVariable Long id){

        salleService.disableSalle(id);

    }

}