package org.example.rawabet.club.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.club.dto.ClubMemberResponseDTO;
import org.example.rawabet.club.entities.Club;
import org.example.rawabet.club.entities.ClubMember;
import org.example.rawabet.club.enums.ClubMemberStatus;
import org.example.rawabet.club.repositories.ClubMemberRepository;
import org.example.rawabet.club.repositories.ClubRepository;
import org.example.rawabet.club.services.interfaces.IClubMemberService;
import org.example.rawabet.entities.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor

public class ClubMemberServiceImpl implements IClubMemberService {

    private final ClubMemberRepository clubMemberRepository;
    private final ClubRepository clubRepository;

    @Override
    @Transactional
    public ClubMemberResponseDTO addMember(User user) {

        clubMemberRepository.findByUser(user)
                .ifPresent(m -> {

                    if(m.getStatus() == ClubMemberStatus.ACTIVE){

                        throw new RuntimeException("Already member");

                    }

                    m.setStatus(ClubMemberStatus.ACTIVE);

                    m.setJoinedAt(LocalDateTime.now());

                    clubMemberRepository.save(m);

                });

        Club club = clubRepository.findById(1L)
                .orElseThrow();

        ClubMember member =
                ClubMember.builder()
                        .user(user)
                        .club(club)
                        .status(ClubMemberStatus.ACTIVE)
                        .joinedAt(LocalDateTime.now())
                        .build();

        return map(clubMemberRepository.save(member));

    }

    @Override
    @Transactional
    public void leaveClub(User user) {

        ClubMember member =
                clubMemberRepository.findByUser(user)
                        .orElseThrow(() ->
                                new RuntimeException("Not a member"));

        if(member.getStatus() == ClubMemberStatus.LEFT){

            throw new RuntimeException("Already left");

        }

        member.setStatus(ClubMemberStatus.LEFT);

        clubMemberRepository.save(member);

    }

    @Override
    public ClubMemberResponseDTO getMember(User user) {

        return clubMemberRepository
                .findByUser(user)
                .filter(m -> m.getStatus() == ClubMemberStatus.ACTIVE)
                .map(this::map)
                .orElse(null);

    }

    @Override
    public List<ClubMemberResponseDTO> getAllMembers() {

        return clubMemberRepository.findAll()
                .stream()
                .map(this::map)
                .toList();

    }

    private ClubMemberResponseDTO map(ClubMember member){

        return ClubMemberResponseDTO.builder()

                .id(member.getId())

                .userId(member.getUser().getId())

                .userName(member.getUser().getNom())

                .status(member.getStatus())

                .joinedAt(member.getJoinedAt())

                .build();

    }

}