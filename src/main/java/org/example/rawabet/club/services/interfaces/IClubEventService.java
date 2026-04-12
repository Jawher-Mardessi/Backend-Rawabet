package org.example.rawabet.club.services.interfaces;

import org.example.rawabet.club.dto.ClubEventRequestDTO;
import org.example.rawabet.club.dto.ClubEventResponseDTO;

import java.util.List;

public interface IClubEventService {

    ClubEventResponseDTO createEvent(ClubEventRequestDTO event);

    List<ClubEventResponseDTO> getAllEvents();

    ClubEventResponseDTO getEvent(Long id);

}