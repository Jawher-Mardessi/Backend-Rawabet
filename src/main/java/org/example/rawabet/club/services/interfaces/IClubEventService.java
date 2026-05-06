package org.example.rawabet.club.services.interfaces;

import org.example.rawabet.club.dto.ClubEventDetailDTO;
import org.example.rawabet.club.dto.ClubEventRequestDTO;
import org.example.rawabet.club.dto.ClubEventResponseDTO;

import java.util.List;

public interface IClubEventService {

    ClubEventResponseDTO createEvent(ClubEventRequestDTO event);

    List<ClubEventResponseDTO> getAllEvents();

    ClubEventResponseDTO getEvent(Long id);

    ClubEventDetailDTO getEventDetail(Long id, boolean withParticipants);

    ClubEventResponseDTO updateEvent(Long id, ClubEventRequestDTO dto);

    void deleteEvent(Long id);
}