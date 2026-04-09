package org.example.rawabet.club.controllers;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.club.dto.ClubMemberResponseDTO;
import org.example.rawabet.club.services.interfaces.IClubMemberService;
import org.example.rawabet.entities.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/club/members")
@RequiredArgsConstructor
public class ClubMemberController {

    private final IClubMemberService clubMemberService;

    // 👤 Mon profil membre
    @GetMapping("/me")
    public ResponseEntity<ClubMemberResponseDTO> getMyMembership() {
        Long userId = getAuthenticatedUserId();
        return ResponseEntity.ok(clubMemberService.getMember(userId));
    }

    // 🚪 Quitter le club
    @PostMapping("/leave")
    public ResponseEntity<Void> leaveClub() {
        Long userId = getAuthenticatedUserId();
        clubMemberService.leaveClub(userId);
        return ResponseEntity.ok().build();
    }

    // 📋 Liste de tous les membres (ACTIVE en premier, LEFT en dessous)
    @GetMapping
    public ResponseEntity<List<ClubMemberResponseDTO>> allMembers() {
        return ResponseEntity.ok(clubMemberService.getAllMembers());
    }

    // Récupération de l'userId depuis le contexte de sécurité
    private Long getAuthenticatedUserId() {
        User user = (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return user.getId();
    }
}