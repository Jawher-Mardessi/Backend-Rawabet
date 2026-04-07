package org.example.rawabet.club.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.rawabet.club.dto.ClubEventRequestDTO;
import org.example.rawabet.club.dto.ClubEventResponseDTO;
import org.example.rawabet.club.services.interfaces.IClubEventService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/club/events")
@RequiredArgsConstructor

public class ClubEventController {

    private final IClubEventService eventService;

    @PostMapping
    @PreAuthorize("hasAuthority('EVENT_CREATE')")
    public ClubEventResponseDTO create(

            @Valid
            @RequestBody ClubEventRequestDTO event){

        return eventService.createEvent(event);

    }

    @GetMapping
    public List<ClubEventResponseDTO> all(){

        return eventService.getAllEvents();

    }

    @GetMapping("/{id}")
    public ClubEventResponseDTO get(

            @PathVariable Long id){

        return eventService.getEvent(id);

    }

}