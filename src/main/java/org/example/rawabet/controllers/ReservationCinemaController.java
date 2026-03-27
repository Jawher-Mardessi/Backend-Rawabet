package org.example.rawabet.controllers;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.entities.ReservationCinema;
import org.example.rawabet.services.IReservationCinemaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")

@RequiredArgsConstructor

public class ReservationCinemaController {

    private final IReservationCinemaService service;

    @PostMapping("/reserver")

    public ReservationCinema reserver(

            @RequestParam Long userId,

            @RequestParam Long seanceId,

            @RequestParam List<Long> seatIds){

        return service.reserverAvecSeats(
                userId,
                seanceId,
                seatIds);

    }

    @GetMapping("/all")

    public List<ReservationCinema> getAll(){

        return service.getAllReservations();

    }

    @GetMapping("/{id}")

    public ReservationCinema getById(
            @PathVariable Long id){

        return service.getReservationById(id);

    }

}