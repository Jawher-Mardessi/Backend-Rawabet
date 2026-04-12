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

    // ✅ CORRIGÉ : retourne 404 si l'utilisateur n'est pas membre (au lieu de 200 + null)
    @GetMapping("/me")
    public ResponseEntity<ClubMemberResponseDTO> getMyMembership() {
        Long userId = getAuthenticatedUserId();
        ClubMemberResponseDTO member = clubMemberService.getMember(userId);
        if (member == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(member);
    }

    @PostMapping("/leave")
    public ResponseEntity<Void> leaveClub() {
        Long userId = getAuthenticatedUserId();
        clubMemberService.leaveClub(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<ClubMemberResponseDTO>> allMembers() {
        return ResponseEntity.ok(clubMemberService.getAllMembers());
    }

    private Long getAuthenticatedUserId() {
        User user = (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return user.getId();
    }
}
