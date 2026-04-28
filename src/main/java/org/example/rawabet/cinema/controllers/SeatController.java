package org.example.rawabet.cinema.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.rawabet.cinema.dto.request.ConfigureHallRequest;
import org.example.rawabet.cinema.dto.response.SeatResponse;
import org.example.rawabet.cinema.dto.response.SeatRowResponse;
import org.example.rawabet.cinema.entities.Seat;
import org.example.rawabet.cinema.repositories.SeatRepository;
import org.example.rawabet.cinema.services.interfaces.ISeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatController {

    private final ISeatService seatService;

    @Autowired
    private SeatRepository seatRepository;

    // Méthodes du main
    @PostMapping("/configure")
    @PreAuthorize("hasAuthority('CINEMA_UPDATE')")
    public void configureHall(@Valid @RequestBody ConfigureHallRequest request) {
        seatService.configureHall(request);
    }

    @GetMapping("/salle/{salleId}")
    public List<SeatResponse> getSeats(@PathVariable Long salleId) {
        return seatService.getSeatsBySalle(salleId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CINEMA_UPDATE')")
    public void disableSeat(@PathVariable Long id) {
        seatService.disableSeat(id);
    }

    @GetMapping("/salle/{salleId}/rows")
    public List<SeatRowResponse> getRows(@PathVariable Long salleId) {
        return seatService.getRowsBySalle(salleId);
    }

    // Ta méthode
    @GetMapping("/seance/{seanceId}")
    public List<Seat> getSeatsBySeance(@PathVariable Long seanceId) {
        return seatRepository.findBySeanceId(seanceId);
    }
}