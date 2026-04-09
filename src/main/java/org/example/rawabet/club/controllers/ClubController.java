package org.example.rawabet.club.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.rawabet.club.dto.ClubRequestDTO;
import org.example.rawabet.club.dto.ClubResponseDTO;
import org.example.rawabet.club.services.interfaces.IClubService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/club")
@RequiredArgsConstructor
public class ClubController {

    private final IClubService clubService;

    // 📖 Récupérer les infos du club (public)
    @GetMapping
    public ResponseEntity<ClubResponseDTO> getClub() {
        return ResponseEntity.ok(clubService.getClub());
    }

    // ✏️ Mettre à jour les infos du club (admin)
    @PutMapping
    public ResponseEntity<ClubResponseDTO> updateClub(@Valid @RequestBody ClubRequestDTO request) {
        return ResponseEntity.ok(clubService.updateClub(request));
    }
}