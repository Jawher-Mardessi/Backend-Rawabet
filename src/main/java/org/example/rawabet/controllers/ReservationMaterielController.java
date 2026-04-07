package org.example.rawabet.controllers;

import jakarta.validation.Valid;
import org.example.rawabet.dto.ExtendReservationMaterielRequestDTO;
import org.example.rawabet.dto.ReservationMaterielRequestDTO;
import org.example.rawabet.dto.ReservationMaterielResponseDTO;
import org.example.rawabet.dto.RetourPartielRequestDTO;
import org.example.rawabet.entities.ReservationMateriel;
import org.example.rawabet.enums.ReservationStatus;
import org.example.rawabet.services.IReservationMaterielService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations-materiel")
public class ReservationMaterielController {

    @Autowired
    private IReservationMaterielService reservationMaterielService;

    // ── Mapping helper ───────────────────────────────────────────

    private ReservationMaterielResponseDTO toResponse(ReservationMateriel r) {
        ReservationMaterielResponseDTO dto = new ReservationMaterielResponseDTO();
        dto.setId(r.getId());
        dto.setQuantite(r.getQuantite());
        dto.setDateDebut(r.getDateDebut());
        dto.setDateFin(r.getDateFin());
        dto.setStatut(r.getStatut());
        dto.setUserId(r.getUser().getId());
        dto.setUserNom(r.getUser().getNom());
        dto.setMaterielId(r.getMateriel().getId());
        dto.setMaterielNom(r.getMateriel().getNom());
        dto.setMaterielReference(r.getMateriel().getReference());
        dto.setPrixUnitaire(r.getMateriel().getPrixUnitaire());
        return dto;
    }

    // ── Core booking ─────────────────────────────────────────────

    @PostMapping("/reserver")
    public ResponseEntity<ReservationMaterielResponseDTO> reserver(
            @Valid @RequestBody ReservationMaterielRequestDTO dto) {
        ReservationMateriel saved = reservationMaterielService.reserverMateriel(
                dto.getUserId(),
                dto.getMaterielId(),
                dto.getQuantite(),
                dto.getDateDebut(),
                dto.getDateFin()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @PutMapping("/{id}/annuler")
    public ResponseEntity<ReservationMaterielResponseDTO> annuler(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(reservationMaterielService.annulerReservation(id)));
    }

    @PutMapping("/{id}/confirmer")
    public ResponseEntity<ReservationMaterielResponseDTO> confirmer(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(reservationMaterielService.confirmerReservation(id)));
    }

    // ── CRUD ─────────────────────────────────────────────────────

    @GetMapping("/{id}")
    public ResponseEntity<ReservationMaterielResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(reservationMaterielService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<List<ReservationMaterielResponseDTO>> getAll() {
        List<ReservationMaterielResponseDTO> result = reservationMaterielService.getAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reservationMaterielService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }

    // ── Filters ──────────────────────────────────────────────────

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReservationMaterielResponseDTO>> getByUser(
            @PathVariable Long userId) {
        List<ReservationMaterielResponseDTO> result = reservationMaterielService.getReservationsByUser(userId)
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/materiel/{materielId}")
    public ResponseEntity<List<ReservationMaterielResponseDTO>> getByMateriel(
            @PathVariable Long materielId) {
        List<ReservationMaterielResponseDTO> result = reservationMaterielService.getReservationsByMateriel(materielId)
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/status/{statut}")
    public ResponseEntity<List<ReservationMaterielResponseDTO>> getByStatus(
            @PathVariable ReservationStatus statut) {
        List<ReservationMaterielResponseDTO> result = reservationMaterielService.getReservationsByStatus(statut)
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
    @PutMapping("/{id}/extend")
    public ResponseEntity<ReservationMaterielResponseDTO> extend(
            @PathVariable Long id,
            @Valid @RequestBody ExtendReservationMaterielRequestDTO dto) {
        return ResponseEntity.ok(toResponse(
                reservationMaterielService.extendReservation(id, dto.getNouvelleDataFin())));
    }

    @PutMapping("/{id}/retour-partiel")
    public ResponseEntity<ReservationMaterielResponseDTO> retourPartiel(
            @PathVariable Long id,
            @Valid @RequestBody RetourPartielRequestDTO dto) {
        return ResponseEntity.ok(toResponse(
                reservationMaterielService.retourPartiel(id, dto.getQuantiteRetournee())));
    }
}