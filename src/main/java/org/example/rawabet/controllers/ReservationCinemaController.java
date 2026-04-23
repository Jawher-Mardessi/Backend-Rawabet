package org.example.rawabet.controllers;

import org.example.rawabet.dto.reservationCinema.request.CreateReservationCinemaRequest;
import org.example.rawabet.dto.reservationCinema.response.ReservationCinemaResponse;
import org.example.rawabet.entities.ReservationCinema;
import org.example.rawabet.entities.User;
import org.example.rawabet.repositories.UserRepository;
import org.example.rawabet.services.IReservationCinemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reservations")
public class ReservationCinemaController {

    @Autowired
    private IReservationCinemaService service;
    @Autowired
    private UserRepository userRepository; // ← doit être ici, au niveau de la classe

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
    public ResponseEntity<?> reserver(@RequestBody CreateReservationCinemaRequest request) {

        User user = (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        request.setUserId(user.getId());

        try {
            return ResponseEntity.ok(service.reserver(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}