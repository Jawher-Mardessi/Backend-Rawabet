package org.example.rawabet.controllers;

import jakarta.validation.Valid;
import org.example.rawabet.dto.SalleEvenementRequestDTO;
import org.example.rawabet.dto.SalleEvenementResponseDTO;
import org.example.rawabet.entities.SalleEvenement;
import org.example.rawabet.enums.SalleStatus;
import org.example.rawabet.enums.SalleType;
import org.example.rawabet.services.ISalleEvenementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/salles")
public class SalleEvenementController {

    @Autowired
    private ISalleEvenementService salleService;

    // ── Mapping helpers ──────────────────────────────────────────

    private SalleEvenement toEntity(SalleEvenementRequestDTO dto) {
        SalleEvenement s = new SalleEvenement();
        s.setNom(dto.getNom());
        s.setCapacite(dto.getCapacite());
        s.setType(dto.getType());
        return s;
    }

    private SalleEvenementResponseDTO toResponse(SalleEvenement s) {
        return new SalleEvenementResponseDTO(
                s.getId(),
                s.getNom(),
                s.getCapacite(),
                s.getType(),
                s.getStatus()
        );
    }

    // ── CRUD ─────────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<SalleEvenementResponseDTO> addSalle(
            @Valid @RequestBody SalleEvenementRequestDTO dto) {
        SalleEvenement saved = salleService.addSalle(toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SalleEvenementResponseDTO> updateSalle(
            @PathVariable Long id,
            @Valid @RequestBody SalleEvenementRequestDTO dto) {
        SalleEvenement salle = toEntity(dto);
        salle.setId(id);
        SalleEvenement updated = salleService.updateSalle(salle);
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSalle(@PathVariable Long id) {
        salleService.deleteSalle(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalleEvenementResponseDTO> getSalleById(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(salleService.getSalleById(id)));
    }

    @GetMapping
    public ResponseEntity<List<SalleEvenementResponseDTO>> getAllSalles() {
        List<SalleEvenementResponseDTO> result = salleService.getAllSalles()
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // ── Availability ─────────────────────────────────────────────

    @GetMapping("/{id}/available")
    public ResponseEntity<Boolean> isSalleAvailable(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin) {
        return ResponseEntity.ok(salleService.isSalleAvailable(id, dateDebut, dateFin));
    }

    @GetMapping("/available")
    public ResponseEntity<List<SalleEvenementResponseDTO>> getAvailableSalles(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin) {
        List<SalleEvenementResponseDTO> result = salleService.getAvailableSalles(dateDebut, dateFin)
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
    @GetMapping("/type/{type}")
    public ResponseEntity<List<SalleEvenementResponseDTO>> getByType(@PathVariable SalleType type) {
        return ResponseEntity.ok(salleService.getSallesByType(type)
                .stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<SalleEvenementResponseDTO> updateStatus(
            @PathVariable Long id, @RequestParam SalleStatus status) {
        return ResponseEntity.ok(toResponse(salleService.updateStatus(id, status)));
    }
}