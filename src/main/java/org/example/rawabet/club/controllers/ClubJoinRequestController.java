package org.example.rawabet.club.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.rawabet.club.dto.ClubJoinRequestDTO;
import org.example.rawabet.club.dto.ClubJoinResponseDTO;
import org.example.rawabet.club.services.interfaces.IClubJoinRequestService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/club/requests")
@RequiredArgsConstructor

public class ClubJoinRequestController {

    private final IClubJoinRequestService joinRequestService;

    @PostMapping
    public ClubJoinResponseDTO joinClub(

            @Valid
            @RequestBody ClubJoinRequestDTO request){

        return joinRequestService.submitRequest(request);

    }

    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('CLUB_MANAGE')")
    public List<ClubJoinResponseDTO> pending(){

        return joinRequestService.pendingRequests();

    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('CLUB_MANAGE')")
    public ClubJoinResponseDTO approve(

            @PathVariable Long id){

        return joinRequestService.approve(id);

    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('CLUB_MANAGE')")
    public ClubJoinResponseDTO reject(

            @PathVariable Long id){

        return joinRequestService.reject(id);

    }

}