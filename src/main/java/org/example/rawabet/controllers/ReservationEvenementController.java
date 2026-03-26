package org.example.rawabet.controllers;

import org.example.rawabet.entities.ReservationEvenement;
import org.example.rawabet.services.IReservationEvenementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations-evenement")
public class ReservationEvenementController {


    @Autowired
    private IReservationEvenementService reservationEvenementService;

    @Autowired
    private org.example.rawabet.services.IAuthService authService;

    @PostMapping
    public ResponseEntity<ReservationEvenement> addReservation(@RequestBody ReservationEvenement reservation) {
        // Always set the authenticated user, ignore any user from the request
        org.example.rawabet.entities.User user = authService.getAuthenticatedUser();
        System.out.println("Authenticated user: " + (user != null ? user.getId() : null));
        reservation.setUser(user);
        // Optionally, clear user from the request body if present
        ReservationEvenement created = reservationEvenementService.addReservation(reservation);
        return ResponseEntity.ok(created);
    }

    @PutMapping
    public ResponseEntity<ReservationEvenement> updateReservation(@RequestBody ReservationEvenement reservation) {
        ReservationEvenement updated = reservationEvenementService.updateReservation(reservation);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationEvenementService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationEvenement> getReservationById(@PathVariable Long id) {
        ReservationEvenement reservation = reservationEvenementService.getReservationById(id);
        if (reservation == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(reservation);
    }

    @GetMapping
    public ResponseEntity<List<ReservationEvenement>> getAllReservations() {
        List<ReservationEvenement> reservations = reservationEvenementService.getAllReservations();
        return ResponseEntity.ok(reservations);
    }
}
