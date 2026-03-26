package org.example.rawabet.controllers;

import org.example.rawabet.entities.SalleEvenement;
import org.example.rawabet.services.ISalleEvenementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salles-evenement")
public class SalleEvenementController {

    @Autowired
    private ISalleEvenementService salleEvenementService;

    @PostMapping
    public ResponseEntity<SalleEvenement> addSalle(@RequestBody SalleEvenement salle) {
        SalleEvenement created = salleEvenementService.addSalle(salle);
        return ResponseEntity.ok(created);
    }

    @PutMapping
    public ResponseEntity<SalleEvenement> updateSalle(@RequestBody SalleEvenement salle) {
        SalleEvenement updated = salleEvenementService.updateSalle(salle);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSalle(@PathVariable Long id) {
        salleEvenementService.deleteSalle(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalleEvenement> getSalleById(@PathVariable Long id) {
        SalleEvenement salle = salleEvenementService.getSalleById(id);
        if (salle == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(salle);
    }

    @GetMapping
    public ResponseEntity<List<SalleEvenement>> getAllSalles() {
        List<SalleEvenement> salles = salleEvenementService.getAllSalles();
        return ResponseEntity.ok(salles);
    }
}
