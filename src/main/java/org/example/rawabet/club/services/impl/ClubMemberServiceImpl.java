package org.example.rawabet.club.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.club.dto.ClubMemberResponseDTO;
import org.example.rawabet.club.entities.Club;
import org.example.rawabet.club.entities.ClubMember;
import org.example.rawabet.club.enums.ClubMemberStatus;
import org.example.rawabet.club.enums.ClubParticipationStatus;
import org.example.rawabet.club.exceptions.BusinessException;
import org.example.rawabet.club.exceptions.NotFoundException;
import org.example.rawabet.club.repositories.ClubMemberRepository;
import org.example.rawabet.club.repositories.ClubEventRepository;
import org.example.rawabet.club.repositories.ClubParticipationRepository;
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
    private final ClubParticipationRepository participationRepository;
    private final ClubEventRepository eventRepository;

    @Override
    @Transactional
    public ClubMemberResponseDTO addMember(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable"));

        return clubMemberRepository.findByUser(user)
                .map(existing -> {
                    if (existing.getStatus() == ClubMemberStatus.ACTIVE) {
                        throw new BusinessException("Déjà membre du club");
                    }
                    // Réactivation (LEFT ou REMOVED)
                    existing.setStatus(ClubMemberStatus.ACTIVE);
                    existing.setJoinedAt(LocalDateTime.now());
                    existing.setRemoveReason(null);
                    existing.setRemovedAt(null);
                    return map(clubMemberRepository.save(existing));
                })
                .orElseGet(() -> {
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

        if (member.getStatus() != ClubMemberStatus.ACTIVE) {
            throw new BusinessException("Vous n'êtes pas membre actif du club");
        }

        member.setStatus(ClubMemberStatus.LEFT);
        clubMemberRepository.save(member);

        // Annuler toutes les réservations CONFIRMED à venir et restituer les places
        participationRepository.findByClubMember(member).stream()
                .filter(p -> p.getStatus() == ClubParticipationStatus.CONFIRMED)
                .filter(p -> p.getClubEvent().getEventDate().isAfter(LocalDateTime.now()))
                .forEach(p -> {
                    participationRepository.decrementReservedPlaces(
                            p.getClubEvent().getId(), p.getReservedPlaces()
                    );
                    participationRepository.updateStatus(p.getId(), ClubParticipationStatus.CANCELLED);
                });
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
                .sorted(Comparator.comparingInt(m -> switch (m.getStatus()) {
                    case ACTIVE  -> 0;
                    case LEFT    -> 1;
                    case REMOVED -> 2;
                }))
                .map(this::map)
                .toList();
    }

    // ✅ AJOUTÉ — expulsion par admin
    @Override
    @Transactional
    public ClubMemberResponseDTO removeMember(Long memberId, String reason) {
        ClubMember member = clubMemberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("Membre introuvable"));

        if (member.getStatus() != ClubMemberStatus.ACTIVE) {
            throw new BusinessException("Ce membre n'est pas actif");
        }

        member.setStatus(ClubMemberStatus.REMOVED);
        member.setRemovedAt(LocalDateTime.now());
        member.setRemoveReason(reason != null ? reason.trim() : null);

        return map(clubMemberRepository.save(member));
    }

    private ClubMemberResponseDTO map(ClubMember member) {
        return ClubMemberResponseDTO.builder()
                .id(member.getId())
                .userId(member.getUser().getId())
                .userName(member.getUser().getNom())
                .status(member.getStatus())
                .joinedAt(member.getJoinedAt())
                .removeReason(member.getRemoveReason())
                .removedAt(member.getRemovedAt())
                .build();
    }
}