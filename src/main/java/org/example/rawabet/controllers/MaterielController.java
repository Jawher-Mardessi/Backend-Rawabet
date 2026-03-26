package org.example.rawabet.controllers;

import org.example.rawabet.entities.Materiel;
import org.example.rawabet.services.IMaterielService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materiels")
public class MaterielController {

    @Autowired
    private IMaterielService materielService;

    @PostMapping
    public ResponseEntity<Materiel> addMateriel(@RequestBody Materiel materiel) {
        Materiel created = materielService.addMateriel(materiel);
        return ResponseEntity.ok(created);
    }

    @PutMapping
    public ResponseEntity<Materiel> updateMateriel(@RequestBody Materiel materiel) {
        Materiel updated = materielService.updateMateriel(materiel);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMateriel(@PathVariable Long id) {
        materielService.deleteMateriel(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Materiel> getMaterielById(@PathVariable Long id) {
        Materiel materiel = materielService.getMaterielById(id);
        if (materiel == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(materiel);
    }

    @GetMapping
    public ResponseEntity<List<Materiel>> getAllMateriels() {
        List<Materiel> materiels = materielService.getAllMateriels();
        return ResponseEntity.ok(materiels);
    }
}
