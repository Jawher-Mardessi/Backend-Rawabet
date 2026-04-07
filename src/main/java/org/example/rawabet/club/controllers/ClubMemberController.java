package org.example.rawabet.club.controllers;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.club.dto.ClubMemberResponseDTO;
import org.example.rawabet.club.services.interfaces.IClubMemberService;
import org.example.rawabet.entities.User;
import org.example.rawabet.services.IAuthService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/club/members")
@RequiredArgsConstructor

public class ClubMemberController {

    private final IClubMemberService clubMemberService;
    private final IAuthService authService;

    @GetMapping("/me")
    public ClubMemberResponseDTO getMyMembership(){

        User user = authService.getAuthenticatedUser();

        return clubMemberService.getMember(user);

    }

    @PostMapping("/leave")
    public void leaveClub(){

        User user = authService.getAuthenticatedUser();

        clubMemberService.leaveClub(user);

    }

    @GetMapping
    public List<ClubMemberResponseDTO> allMembers(){

        return clubMemberService.getAllMembers();

    }

}