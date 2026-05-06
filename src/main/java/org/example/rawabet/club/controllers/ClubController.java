package org.example.rawabet.club.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.rawabet.club.dto.ClubRequestDTO;
import org.example.rawabet.club.dto.ClubResponseDTO;
import org.example.rawabet.club.services.interfaces.IClubService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/club")
@RequiredArgsConstructor
public class ClubController {

    private final IClubService clubService;

    // ✅ Public — tout le monde peut voir les infos du club
    @GetMapping
    public ResponseEntity<ClubResponseDTO> getClub() {
        return ResponseEntity.ok(clubService.getClub());
    }

    // ✅ FIX : @PreAuthorize ajouté — seul un admin club peut modifier le club
    @PutMapping
    @PreAuthorize("hasAuthority('CLUB_MANAGE')")
    public ResponseEntity<ClubResponseDTO> updateClub(
            @Valid @RequestBody ClubRequestDTO request) {
        return ResponseEntity.ok(clubService.updateClub(request));
    }
}