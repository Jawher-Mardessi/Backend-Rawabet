package org.example.rawabet.club.repositories;

import org.example.rawabet.club.entities.ClubEvent;
import org.example.rawabet.club.entities.ClubMember;
import org.example.rawabet.club.entities.ClubParticipation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClubParticipationRepository extends JpaRepository<ClubParticipation, Long> {

    Optional<ClubParticipation> findByClubMemberAndClubEvent(
            ClubMember member,
            ClubEvent event
    );

    List<ClubParticipation> findByClubMember(ClubMember member);

}