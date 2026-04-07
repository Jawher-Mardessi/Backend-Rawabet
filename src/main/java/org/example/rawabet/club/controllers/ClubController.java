package org.example.rawabet.club.controllers;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.club.entities.Club;
import org.example.rawabet.club.services.interfaces.IClubService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/club")
@RequiredArgsConstructor

public class ClubController {

    private final IClubService clubService;

    @GetMapping
    public Club getClub(){

        return clubService.getClub();

    }

    @PutMapping
    public Club updateClub(@RequestBody Club club){

        return clubService.updateClub(club);

    }

}