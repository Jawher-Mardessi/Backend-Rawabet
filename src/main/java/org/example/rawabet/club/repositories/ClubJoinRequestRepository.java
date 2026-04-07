package org.example.rawabet.club.repositories;

import org.example.rawabet.club.entities.ClubJoinRequest;
import org.example.rawabet.club.enums.ClubJoinRequestStatus;
import org.example.rawabet.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClubJoinRequestRepository extends JpaRepository<ClubJoinRequest, Long> {

    Optional<ClubJoinRequest> findByUserAndStatus(User user, ClubJoinRequestStatus status);

    List<ClubJoinRequest> findByStatus(ClubJoinRequestStatus status);

}