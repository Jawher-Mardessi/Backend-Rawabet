package org.example.rawabet.club.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.club.dto.ClubJoinRequestDTO;
import org.example.rawabet.club.dto.ClubJoinResponseDTO;
import org.example.rawabet.club.entities.ClubJoinRequest;
import org.example.rawabet.club.enums.ClubJoinRequestStatus;
import org.example.rawabet.club.exceptions.BusinessException;
import org.example.rawabet.club.exceptions.NotFoundException;
import org.example.rawabet.club.repositories.ClubJoinRequestRepository;
import org.example.rawabet.club.services.interfaces.IClubJoinRequestService;
import org.example.rawabet.club.services.interfaces.IClubMemberService;
import org.example.rawabet.entities.User;
import org.example.rawabet.services.IAuthService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor

public class ClubJoinRequestServiceImpl implements IClubJoinRequestService {

    private final ClubJoinRequestRepository joinRequestRepository;
    private final IAuthService authService;
    private final IClubMemberService clubMemberService;

    @Override
    public ClubJoinResponseDTO submitRequest(ClubJoinRequestDTO dto) {

        User user = authService.getAuthenticatedUser();

        if(clubMemberService.getMember(user) != null){

            throw new BusinessException("Already a club member");

        }

        joinRequestRepository
                .findByUserAndStatus(user, ClubJoinRequestStatus.PENDING)
                .ifPresent(r -> {

                    throw new BusinessException("Pending request already exists");

                });

        ClubJoinRequest request =
                ClubJoinRequest.builder()
                        .user(user)
                        .motivation(dto.getMotivation())
                        .status(ClubJoinRequestStatus.PENDING)
                        .requestDate(LocalDateTime.now())
                        .build();

        return map(joinRequestRepository.save(request));

    }

    @Override
    @Transactional
    public ClubJoinResponseDTO approve(Long id) {

        ClubJoinRequest request =
                joinRequestRepository.findById(id)
                        .orElseThrow(() ->
                                new NotFoundException("Request not found"));

        if(request.getStatus() != ClubJoinRequestStatus.PENDING){

            throw new BusinessException("Request already processed");

        }

        request.setStatus(ClubJoinRequestStatus.APPROVED);

        request.setProcessedDate(LocalDateTime.now());

        clubMemberService.addMember(request.getUser());

        return map(request);

    }

    @Override
    @Transactional
    public ClubJoinResponseDTO reject(Long id) {

        ClubJoinRequest request =
                joinRequestRepository.findById(id)
                        .orElseThrow(() ->
                                new NotFoundException("Request not found"));

        if(request.getStatus() != ClubJoinRequestStatus.PENDING){

            throw new BusinessException("Request already processed");

        }

        request.setStatus(ClubJoinRequestStatus.REJECTED);

        request.setProcessedDate(LocalDateTime.now());

        return map(request);

    }

    @Override
    public List<ClubJoinResponseDTO> pendingRequests() {

        return joinRequestRepository
                .findByStatus(ClubJoinRequestStatus.PENDING)
                .stream()
                .map(this::map)
                .toList();

    }

    private ClubJoinResponseDTO map(ClubJoinRequest request){

        return ClubJoinResponseDTO.builder()

                .id(request.getId())

                .userId(request.getUser().getId())

                .userName(request.getUser().getNom())

                .motivation(request.getMotivation())

                .status(request.getStatus())

                .requestDate(request.getRequestDate())

                .processedDate(request.getProcessedDate())

                .build();

    }

}