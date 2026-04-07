package org.example.rawabet.club.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.rawabet.club.dto.ClubParticipationRequestDTO;
import org.example.rawabet.club.dto.ClubParticipationResponseDTO;
import org.example.rawabet.club.services.interfaces.IClubParticipationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/club/reservations")
@RequiredArgsConstructor

public class ClubParticipationController {

    private final IClubParticipationService participationService;

    @PostMapping
    public ClubParticipationResponseDTO reserve(

            @Valid
            @RequestBody ClubParticipationRequestDTO request){

        return participationService.reserve(request);

    }

    @DeleteMapping("/{id}")
    public void cancel(

            @PathVariable Long id){

        participationService.cancel(id);

    }

    @GetMapping("/my")
    public List<ClubParticipationResponseDTO> my(){

        return participationService.myReservations();

    }

}