package org.example.rawabet.club.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.rawabet.club.dto.ClubEventDetailDTO;
import org.example.rawabet.club.dto.ClubEventRequestDTO;
import org.example.rawabet.club.dto.ClubEventResponseDTO;
import org.example.rawabet.club.services.interfaces.IClubEventService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/club/events")
@RequiredArgsConstructor
public class ClubEventController {

    private final IClubEventService eventService;

    @PostMapping
    @PreAuthorize("hasAuthority('CLUB_CREATE')")
    public ClubEventResponseDTO create(@Valid @RequestBody ClubEventRequestDTO event) {
        return eventService.createEvent(event);
    }

    @GetMapping
    public List<ClubEventResponseDTO> all() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public ClubEventResponseDTO get(@PathVariable Long id) {
        return eventService.getEvent(id);
    }

    @GetMapping("/{id}/detail")
    public ClubEventDetailDTO detail(@PathVariable Long id, Authentication authentication) {
        boolean isAdmin = authentication != null
                && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("CLUB_MANAGE")
                        || a.getAuthority().equals("SUPER_ADMIN"));
        return eventService.getEventDetail(id, isAdmin);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CLUB_CREATE')")
    public ClubEventResponseDTO update(@PathVariable Long id,
                                       @Valid @RequestBody ClubEventRequestDTO dto) {
        return eventService.updateEvent(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CLUB_CREATE')")
    public void delete(@PathVariable Long id) {
        eventService.deleteEvent(id);
    }
}