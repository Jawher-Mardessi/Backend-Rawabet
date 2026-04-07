package org.example.rawabet.controllers;

import org.example.rawabet.dto.reservationCinema.request.CreateReservationCinemaRequest;
import org.example.rawabet.dto.reservationCinema.response.ReservationCinemaResponse;
import org.example.rawabet.entities.ReservationCinema;
import org.example.rawabet.services.IReservationCinemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationCinemaController {

    @Autowired
    private IReservationCinemaService service;

    @PostMapping("/add")
    public ReservationCinema add(@RequestBody ReservationCinema r) {
        return service.addReservation(r);
    }

    @PutMapping("/update")
    public ReservationCinema update(@RequestBody ReservationCinema r) {
        return service.updateReservation(r);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteReservation(id);
    }

    @GetMapping("/{id}")
    public ReservationCinema getById(@PathVariable Long id) {
        return service.getReservationById(id);
    }

    @GetMapping("/all")
    public List<ReservationCinema> getAll() {
        return service.getAllReservations();
    }

    @PostMapping("/reserver")
    public ReservationCinemaResponse reserver(@RequestBody CreateReservationCinemaRequest request) {
        return service.reserver(request);
    }
}