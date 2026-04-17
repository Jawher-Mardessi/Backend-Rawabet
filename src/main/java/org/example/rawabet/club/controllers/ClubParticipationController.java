package org.example.rawabet.club.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.rawabet.club.dto.ClubParticipationRequestDTO;
import org.example.rawabet.club.dto.ClubParticipationResponseDTO;
import org.example.rawabet.club.services.interfaces.IClubParticipationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/club/reservations")
@RequiredArgsConstructor
public class ClubParticipationController {

    private final IClubParticipationService participationService;

    @PostMapping
    public ResponseEntity<ClubParticipationResponseDTO> reserve(
            @Valid @RequestBody ClubParticipationRequestDTO request) {
        return ResponseEntity.ok(participationService.reserve(request));
    }

    // ✅ FIX : retourne ResponseEntity<Void> — les exceptions (BusinessException,
    //          NotFoundException) sont maintenant interceptées par GlobalClubExceptionHandler
    //          et renvoient un 400/404 lisible au frontend Angular.
    @PutMapping("/{id}")
    public ResponseEntity<ClubParticipationResponseDTO> update(
            @PathVariable Long id,
            @RequestParam @jakarta.validation.constraints.Min(1) int places) {
        return ResponseEntity.ok(participationService.updateReservation(id, places));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        participationService.cancel(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<ClubParticipationResponseDTO>> my() {
        return ResponseEntity.ok(participationService.myReservations());
    }
}