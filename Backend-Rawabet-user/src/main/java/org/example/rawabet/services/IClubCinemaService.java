package org.example.rawabet.services;

import org.example.rawabet.entities.ClubCinema;

import java.util.List;

public interface IClubCinemaService {

    ClubCinema addClub(ClubCinema club);

    ClubCinema updateClub(ClubCinema club);

    void deleteClub(Long id);

    ClubCinema getClubById(Long id);

    List<ClubCinema> getAllClubs();
}