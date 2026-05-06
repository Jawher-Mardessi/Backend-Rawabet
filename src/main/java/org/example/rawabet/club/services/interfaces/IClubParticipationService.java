package org.example.rawabet.club.services.interfaces;

import org.example.rawabet.club.dto.ClubParticipationRequestDTO;
import org.example.rawabet.club.dto.ClubParticipationResponseDTO;

import java.util.List;

public interface IClubParticipationService {

    ClubParticipationResponseDTO reserve(ClubParticipationRequestDTO request);

    ClubParticipationResponseDTO updateReservation(Long participationId, int places);

    void cancel(Long participationId);

    List<ClubParticipationResponseDTO> myReservations();

}