package org.example.rawabet.controllers;

import org.example.rawabet.entities.ReservationMateriel;
import org.example.rawabet.services.IService.materiel.IReservationMaterielService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations-materiel")
public class ReservationMaterielController {

    @Autowired
    private IReservationMaterielService reservationMaterielService;

    @PostMapping
    public ResponseEntity<ReservationMateriel> addReservation(@RequestBody ReservationMateriel rm) {
        ReservationMateriel created = reservationMaterielService.addReservation(rm);
        return ResponseEntity.ok(created);
    }

    @PutMapping
    public ResponseEntity<ReservationMateriel> updateReservation(@RequestBody ReservationMateriel rm) {
        ReservationMateriel updated = reservationMaterielService.updateReservation(rm);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationMaterielService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationMateriel> getById(@PathVariable Long id) {
        ReservationMateriel rm = reservationMaterielService.getById(id);
        if (rm == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rm);
    }

    @GetMapping
    public ResponseEntity<List<ReservationMateriel>> getAll() {
        List<ReservationMateriel> rms = reservationMaterielService.getAll();
        return ResponseEntity.ok(rms);
    }
}
