package org.example.rawabet.club.repositories;

import org.example.rawabet.club.entities.ClubEvent;
import org.example.rawabet.club.entities.ClubMember;
import org.example.rawabet.club.entities.ClubParticipation;
import org.example.rawabet.club.enums.ClubParticipationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClubParticipationRepository extends JpaRepository<ClubParticipation, Long> {

    Optional<ClubParticipation> findByClubMemberAndClubEvent(ClubMember member, ClubEvent event);

    List<ClubParticipation> findByClubMember(ClubMember member);

    List<ClubParticipation> findByClubEventAndStatus(ClubEvent event, ClubParticipationStatus status);

    void deleteByClubEvent(ClubEvent event);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ClubParticipation p SET p.status = :status WHERE p.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") ClubParticipationStatus status);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ClubEvent e SET e.reservedPlaces = GREATEST(0, e.reservedPlaces - :places) WHERE e.id = :eventId")
    void decrementReservedPlaces(@Param("eventId") Long eventId, @Param("places") int places);
}