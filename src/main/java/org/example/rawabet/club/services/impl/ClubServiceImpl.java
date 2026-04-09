package org.example.rawabet.club.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.club.dto.ClubRequestDTO;
import org.example.rawabet.club.dto.ClubResponseDTO;
import org.example.rawabet.club.entities.Club;
import org.example.rawabet.club.repositories.ClubRepository;
import org.example.rawabet.club.services.interfaces.IClubService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ClubServiceImpl implements IClubService {

    private final ClubRepository clubRepository;

    @Override
    public ClubResponseDTO getClub() {

        Club club = clubRepository.findById(1L)
                .orElseGet(() -> {
                    Club newClub = Club.builder()
                            .id(1L)
                            .name("Club Culturel")
                            .description("Club universitaire")
                            .createdAt(LocalDateTime.now())
                            .build();
                    return clubRepository.save(newClub);
                });

        return toDTO(club);
    }

    @Override
    public ClubResponseDTO updateClub(ClubRequestDTO request) {

        Club club = clubRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Club introuvable"));

        club.setName(request.getName());
        club.setDescription(request.getDescription());

        return toDTO(clubRepository.save(club));
    }

    private ClubResponseDTO toDTO(Club club) {
        return ClubResponseDTO.builder()
                .id(club.getId())
                .name(club.getName())
                .description(club.getDescription())
                .createdAt(club.getCreatedAt())
                .build();
    }
}