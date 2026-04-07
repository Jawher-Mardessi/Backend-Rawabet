package org.example.rawabet.club.services.interfaces;

import org.example.rawabet.club.dto.ClubMemberResponseDTO;
import org.example.rawabet.entities.User;

import java.util.List;

public interface IClubMemberService {

    ClubMemberResponseDTO addMember(User user);

    void leaveClub(User user);

    ClubMemberResponseDTO getMember(User user);

    List<ClubMemberResponseDTO> getAllMembers();

}