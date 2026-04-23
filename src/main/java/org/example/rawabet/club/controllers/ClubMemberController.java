package org.example.rawabet.club.controllers;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.club.dto.ClubMemberResponseDTO;
import org.example.rawabet.club.services.interfaces.IClubMemberService;
import org.example.rawabet.entities.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/club/members")
@RequiredArgsConstructor
public class ClubMemberController {

    private final IClubMemberService clubMemberService;

    @GetMapping("/me")
    public ResponseEntity<ClubMemberResponseDTO> getMyMembership() {
        Long userId = getAuthenticatedUserId();
        ClubMemberResponseDTO member = clubMemberService.getMember(userId);
        if (member == null) return ResponseEntity.notFound().build();
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

    // ✅ AJOUTÉ — expulsion par admin
    // Body JSON optionnel : { "reason": "Inactif depuis 3 mois" }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CLUB_MANAGE')")
    public ResponseEntity<ClubMemberResponseDTO> removeMember(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {

        String reason = body != null ? body.get("reason") : null;
        return ResponseEntity.ok(clubMemberService.removeMember(id, reason));
    }

    private Long getAuthenticatedUserId() {
        User user = (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return user.getId();
    }
}