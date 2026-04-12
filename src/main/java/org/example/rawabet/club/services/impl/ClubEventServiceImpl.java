package org.example.rawabet.club.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.club.dto.ClubEventRequestDTO;
import org.example.rawabet.club.dto.ClubEventResponseDTO;
import org.example.rawabet.club.entities.Club;
import org.example.rawabet.club.entities.ClubEvent;
import org.example.rawabet.club.exceptions.BusinessException;
import org.example.rawabet.club.exceptions.NotFoundException;
import org.example.rawabet.club.repositories.ClubEventRepository;
import org.example.rawabet.club.repositories.ClubRepository;
import org.example.rawabet.club.services.interfaces.IClubEventService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor

public class ClubEventServiceImpl implements IClubEventService {

    private final ClubEventRepository eventRepository;
    private final ClubRepository clubRepository;

    @Override
    public ClubEventResponseDTO createEvent(ClubEventRequestDTO dto) {

        if(dto.getEventDate().isBefore(LocalDateTime.now())){

            throw new BusinessException("Event date invalid");

        }

        Club club = clubRepository.findById(1L)
                .orElseThrow(() ->
                        new NotFoundException("Club not found"));

        ClubEvent event =
                ClubEvent.builder()
                        .club(club)
                        .title(dto.getTitle())
                        .description(dto.getDescription())
                        .eventDate(dto.getEventDate())
                        .maxPlaces(dto.getMaxPlaces())
                        .reservedPlaces(0)
                        .posterUrl(dto.getPosterUrl())
                        .createdAt(LocalDateTime.now())
                        .build();

        return map(eventRepository.save(event));

    }

    @Override
    public List<ClubEventResponseDTO> getAllEvents() {

        return eventRepository.findAll()
                .stream()
                .map(this::map)
                .toList();

    }

    @Override
    public ClubEventResponseDTO getEvent(Long id) {

        ClubEvent event =
                eventRepository.findById(id)
                        .orElseThrow(() ->
                                new NotFoundException("Event not found"));

        return map(event);

    }

    private ClubEventResponseDTO map(ClubEvent event){

        return ClubEventResponseDTO.builder()

                .id(event.getId())

                .title(event.getTitle())

                .description(event.getDescription())

                .eventDate(event.getEventDate())

                .maxPlaces(event.getMaxPlaces())

                .reservedPlaces(event.getReservedPlaces())

                .remainingPlaces(event.getRemainingPlaces())

                .posterUrl(event.getPosterUrl())

                .createdAt(event.getCreatedAt())

                .build();

    }

}