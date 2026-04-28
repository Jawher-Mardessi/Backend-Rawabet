package org.example.rawabet.club.services.interfaces;

import org.example.rawabet.club.dto.ClubJoinRequestDTO;
import org.example.rawabet.club.dto.ClubJoinResponseDTO;

import java.util.List;
import java.util.Optional;

public interface IClubJoinRequestService {

    ClubJoinResponseDTO submitRequest(ClubJoinRequestDTO request);

    ClubJoinResponseDTO approve(Long requestId);

    ClubJoinResponseDTO reject(Long requestId);

    List<ClubJoinResponseDTO> pendingRequests();

    // ✅ AJOUT : récupérer la dernière demande de l'utilisateur connecté
    Optional<ClubJoinResponseDTO> getMyRequest();
}
