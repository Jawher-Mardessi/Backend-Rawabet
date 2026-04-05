package org.example.rawabet.controllers;

import org.example.rawabet.entities.Evenement;
import org.example.rawabet.services.IService.evenement.IEvenementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evenements")
public class EvenementController {

    @Autowired
    private IEvenementService evenementService;

    @PostMapping
    public ResponseEntity<Evenement> addEvenement(@RequestBody Evenement evenement) {
        Evenement created = evenementService.addEvenement(evenement);
        return ResponseEntity.ok(created);
    }

    @PutMapping
    public ResponseEntity<Evenement> updateEvenement(@RequestBody Evenement evenement) {
        Evenement updated = evenementService.updateEvenement(evenement);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvenement(@PathVariable Long id) {
        evenementService.deleteEvenement(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evenement> getEvenementById(@PathVariable Long id) {
        Evenement evenement = evenementService.getEvenementById(id);
        if (evenement == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(evenement);
    }

    @GetMapping
    public ResponseEntity<List<Evenement>> getAllEvenements() {
        List<Evenement> evenements = evenementService.getAllEvenements();
        return ResponseEntity.ok(evenements);
    }
}
