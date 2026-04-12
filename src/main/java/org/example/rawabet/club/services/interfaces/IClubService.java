package org.example.rawabet.club.services.interfaces;

import org.example.rawabet.club.dto.ClubRequestDTO;
import org.example.rawabet.club.dto.ClubResponseDTO;

public interface IClubService {

    ClubResponseDTO getClub();

    ClubResponseDTO updateClub(ClubRequestDTO request);
}