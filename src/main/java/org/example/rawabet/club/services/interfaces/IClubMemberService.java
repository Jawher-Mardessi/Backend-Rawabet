package org.example.rawabet.club.services.interfaces;

import org.example.rawabet.club.dto.ClubMemberResponseDTO;

import java.util.List;

public interface IClubMemberService {

    ClubMemberResponseDTO addMember(Long userId);

    void leaveClub(Long userId);

    ClubMemberResponseDTO getMember(Long userId);

    List<ClubMemberResponseDTO> getAllMembers();

    // ✅ AJOUTÉ — expulsion par admin
    ClubMemberResponseDTO removeMember(Long memberId, String reason);
}