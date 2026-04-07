package org.example.rawabet.club.repositories;

import org.example.rawabet.club.entities.ClubMember;
import org.example.rawabet.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClubMemberRepository extends JpaRepository<ClubMember, Long> {

    Optional<ClubMember> findByUser(User user);

}