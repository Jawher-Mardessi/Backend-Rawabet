package org.example.rawabet.club.services.interfaces;

import org.example.rawabet.club.dto.ClubJoinRequestDTO;
import org.example.rawabet.club.dto.ClubJoinResponseDTO;

import java.util.List;

public interface IClubJoinRequestService {

    ClubJoinResponseDTO submitRequest(ClubJoinRequestDTO request);

    ClubJoinResponseDTO approve(Long requestId);

    ClubJoinResponseDTO reject(Long requestId);

    List<ClubJoinResponseDTO> pendingRequests();

}