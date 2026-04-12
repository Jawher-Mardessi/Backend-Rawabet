package org.example.rawabet.club.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.club.dto.ClubMemberResponseDTO;
import org.example.rawabet.club.entities.Club;
import org.example.rawabet.club.entities.ClubMember;
import org.example.rawabet.club.enums.ClubMemberStatus;
import org.example.rawabet.club.exceptions.BusinessException;
import org.example.rawabet.club.exceptions.NotFoundException;
import org.example.rawabet.club.repositories.ClubMemberRepository;
import org.example.rawabet.club.repositories.ClubRepository;
import org.example.rawabet.club.services.interfaces.IClubMemberService;
import org.example.rawabet.entities.User;
import org.example.rawabet.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClubMemberServiceImpl implements IClubMemberService {

    private final ClubMemberRepository clubMemberRepository;
    private final ClubRepository clubRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ClubMemberResponseDTO addMember(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable"));

        // Si l'utilisateur a déjà un historique (ex: il avait quitté) → on le réactive
        return clubMemberRepository.findByUser(user)
                .map(existing -> {
                    if (existing.getStatus() == ClubMemberStatus.ACTIVE) {
                        throw new BusinessException("Déjà membre du club");
                    }
                    // Réactivation de l'ancien membre
                    existing.setStatus(ClubMemberStatus.ACTIVE);
                    existing.setJoinedAt(LocalDateTime.now());
                    return map(clubMemberRepository.save(existing));
                })
                .orElseGet(() -> {
                    // Première adhésion
                    Club club = clubRepository.findById(1L)
                            .orElseThrow(() -> new NotFoundException("Club introuvable"));

                    ClubMember member = ClubMember.builder()
                            .user(user)
                            .club(club)
                            .status(ClubMemberStatus.ACTIVE)
                            .joinedAt(LocalDateTime.now())
                            .build();

                    return map(clubMemberRepository.save(member));
                });
    }

    @Override
    @Transactional
    public void leaveClub(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable"));

        ClubMember member = clubMemberRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException("Vous n'êtes pas membre du club"));

        if (member.getStatus() == ClubMemberStatus.LEFT) {
            throw new BusinessException("Vous avez déjà quitté le club");
        }

        member.setStatus(ClubMemberStatus.LEFT);
        clubMemberRepository.save(member);
    }

    @Override
    public ClubMemberResponseDTO getMember(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable"));

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
                // ACTIVE en premier, LEFT en dessous
                .sorted(Comparator.comparing(m ->
                        m.getStatus() == ClubMemberStatus.ACTIVE ? 0 : 1))
                .map(this::map)
                .toList();
    }

    private ClubMemberResponseDTO map(ClubMember member) {
        return ClubMemberResponseDTO.builder()
                .id(member.getId())
                .userId(member.getUser().getId())
                .userName(member.getUser().getNom())
                .status(member.getStatus())
                .joinedAt(member.getJoinedAt())
                .build();
    }
}