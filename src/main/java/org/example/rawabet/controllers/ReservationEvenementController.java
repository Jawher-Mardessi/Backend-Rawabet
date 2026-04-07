package org.example.rawabet.controllers;

import jakarta.validation.Valid;
import org.example.rawabet.dto.ReservationEvenementRequestDTO;
import org.example.rawabet.dto.ReservationEvenementResponseDTO;
import org.example.rawabet.entities.ReservationEvenement;
import org.example.rawabet.enums.ReservationStatus;
import org.example.rawabet.services.IReservationEvenementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations-evenement")
public class ReservationEvenementController {

    @Autowired
    private IReservationEvenementService reservationService;

    // ── Mapping helper ───────────────────────────────────────────

    private ReservationEvenementResponseDTO toResponse(ReservationEvenement r) {
        ReservationEvenementResponseDTO dto = new ReservationEvenementResponseDTO();
        dto.setId(r.getId());
        dto.setDateReservation(r.getDateReservation());
        dto.setDateExpiration(r.getDateExpiration());   // ✅ add this
        dto.setStatut(r.getStatut());
        dto.setEnAttente(r.isEnAttente());              // ✅ add this
        dto.setUserId(r.getUser().getId());
        dto.setUserNom(r.getUser().getNom());
        dto.setEvenementId(r.getEvenement().getId());
        dto.setEvenementTitre(r.getEvenement().getTitre());
        dto.setEvenementDateDebut(r.getEvenement().getDateDebut());
        dto.setEvenementDateFin(r.getEvenement().getDateFin());
        if (r.getEvenement().getSalle() != null)
            dto.setSalleNom(r.getEvenement().getSalle().getNom());
        return dto;
    }
    // ── Core booking ─────────────────────────────────────────────

    @PostMapping("/reserver")
    public ResponseEntity<ReservationEvenementResponseDTO> reserver(
            @Valid @RequestBody ReservationEvenementRequestDTO dto) {
        ReservationEvenement saved = reservationService.reserverEvenement(
                dto.getUserId(), dto.getEvenementId());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @PutMapping("/{id}/annuler")
    public ResponseEntity<ReservationEvenementResponseDTO> annuler(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(reservationService.annulerReservation(id)));
    }

    @PutMapping("/{id}/confirmer")
    public ResponseEntity<ReservationEvenementResponseDTO> confirmer(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(reservationService.confirmerReservation(id)));
    }

    // ── CRUD ─────────────────────────────────────────────────────

    @GetMapping("/{id}")
    public ResponseEntity<ReservationEvenementResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(reservationService.getReservationById(id)));
    }

    @GetMapping
    public ResponseEntity<List<ReservationEvenementResponseDTO>> getAll() {
        List<ReservationEvenementResponseDTO> result = reservationService.getAllReservations()
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }

    // ── Filters ──────────────────────────────────────────────────

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReservationEvenementResponseDTO>> getByUser(
            @PathVariable Long userId) {
        List<ReservationEvenementResponseDTO> result = reservationService.getReservationsByUser(userId)
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/evenement/{evenementId}")
    public ResponseEntity<List<ReservationEvenementResponseDTO>> getByEvenement(
            @PathVariable Long evenementId) {
        List<ReservationEvenementResponseDTO> result = reservationService.getReservationsByEvenement(evenementId)
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/status/{statut}")
    public ResponseEntity<List<ReservationEvenementResponseDTO>> getByStatus(
            @PathVariable ReservationStatus statut) {
        List<ReservationEvenementResponseDTO> result = reservationService.getReservationsByStatus(statut)
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkUserReservation(
            @RequestParam Long userId,
            @RequestParam Long evenementId) {
        return ResponseEntity.ok(reservationService.userAlreadyReserved(userId, evenementId));
    }
    @GetMapping("/evenement/{evenementId}/waitlist")
    public ResponseEntity<List<ReservationEvenementResponseDTO>> getWaitlist(
            @PathVariable Long evenementId) {
        return ResponseEntity.ok(reservationService.getWaitlistByEvenement(evenementId)
                .stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/user/{userId}/waitlist")
    public ResponseEntity<List<ReservationEvenementResponseDTO>> getUserWaitlist(
            @PathVariable Long userId) {
        return ResponseEntity.ok(reservationService.getUserWaitlist(userId)
                .stream().map(this::toResponse).collect(Collectors.toList()));
    }
}