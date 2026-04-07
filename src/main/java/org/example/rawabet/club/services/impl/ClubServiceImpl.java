package org.example.rawabet.club.services.impl;

import lombok.RequiredArgsConstructor;
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
    public Club getClub() {

        return clubRepository.findById(1L)
                .orElseGet(() -> {

                    Club club = Club.builder()
                            .id(1L)
                            .name("Club Culturel")
                            .description("Club universitaire")
                            .createdAt(LocalDateTime.now())
                            .build();

                    return clubRepository.save(club);

                });

    }

    @Override
    public Club updateClub(Club club) {

        club.setId(1L);

        return clubRepository.save(club);

    }

}