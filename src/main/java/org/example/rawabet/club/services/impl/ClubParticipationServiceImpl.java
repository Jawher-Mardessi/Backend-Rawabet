package org.example.rawabet.club.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.club.dto.ClubParticipationRequestDTO;
import org.example.rawabet.club.dto.ClubParticipationResponseDTO;
import org.example.rawabet.club.entities.ClubEvent;
import org.example.rawabet.club.entities.ClubMember;
import org.example.rawabet.club.entities.ClubParticipation;
import org.example.rawabet.club.enums.ClubMemberStatus;
import org.example.rawabet.club.enums.ClubParticipationStatus;
import org.example.rawabet.club.exceptions.BusinessException;
import org.example.rawabet.club.exceptions.NotFoundException;
import org.example.rawabet.club.repositories.ClubEventRepository;
import org.example.rawabet.club.repositories.ClubMemberRepository;
import org.example.rawabet.club.repositories.ClubParticipationRepository;
import org.example.rawabet.club.services.interfaces.IClubParticipationService;
import org.example.rawabet.entities.User;
import org.example.rawabet.services.IAuthService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor

public class ClubParticipationServiceImpl implements IClubParticipationService {

    private final ClubParticipationRepository participationRepository;
    private final ClubEventRepository eventRepository;
    private final ClubMemberRepository memberRepository;
    private final IAuthService authService;

    @Override
    @Transactional
    public ClubParticipationResponseDTO reserve(ClubParticipationRequestDTO dto){

        User user = authService.getAuthenticatedUser();

        ClubMember member =
                memberRepository.findByUser(user)
                        .orElseThrow(() ->
                                new BusinessException("Not a club member"));

        if(member.getStatus() != ClubMemberStatus.ACTIVE){

            throw new BusinessException("Inactive member");

        }

        ClubEvent event =
                eventRepository.findById(dto.getEventId())
                        .orElseThrow(() ->
                                new NotFoundException("Event not found"));

        if(event.getEventDate().isBefore(LocalDateTime.now())){

            throw new BusinessException("Event finished");

        }

        participationRepository
                .findByClubMemberAndClubEvent(member,event)
                .ifPresent(p -> {

                    if(p.getStatus() == ClubParticipationStatus.CONFIRMED){

                        throw new BusinessException("Already reserved");

                    }

                });

        if(event.getReservedPlaces() + dto.getPlaces()
                > event.getMaxPlaces()){

            throw new BusinessException("Not enough places");

        }

        event.setReservedPlaces(
                event.getReservedPlaces() + dto.getPlaces()
        );

        ClubParticipation participation =
                ClubParticipation.builder()
                        .clubMember(member)
                        .clubEvent(event)
                        .reservedPlaces(dto.getPlaces())
                        .status(ClubParticipationStatus.CONFIRMED)
                        .reservationDate(LocalDateTime.now())
                        .build();

        return map(participationRepository.save(participation));

    }

    @Override
    @Transactional
    public void cancel(Long id){

        User user = authService.getAuthenticatedUser();

        ClubParticipation participation =
                participationRepository.findById(id)
                        .orElseThrow(() ->
                                new NotFoundException("Reservation not found"));

        if(!participation.getClubMember()
                .getUser()
                .getId()
                .equals(user.getId())){

            throw new BusinessException("Not your reservation");

        }

        if(participation.getStatus()
                == ClubParticipationStatus.CANCELLED){

            return;

        }

        ClubEvent event = participation.getClubEvent();

        if(event.getEventDate().isBefore(LocalDateTime.now())){

            throw new BusinessException("Event already finished");

        }

        event.setReservedPlaces(
                event.getReservedPlaces()
                        - participation.getReservedPlaces()
        );

        if(event.getReservedPlaces() < 0){

            event.setReservedPlaces(0);

        }

        participation.setStatus(
                ClubParticipationStatus.CANCELLED
        );

        participationRepository.save(participation);

    }

    @Override
    public List<ClubParticipationResponseDTO> myReservations(){

        User user = authService.getAuthenticatedUser();

        ClubMember member =
                memberRepository.findByUser(user)
                        .orElseThrow(() ->
                                new BusinessException("Not member"));

        return participationRepository
                .findByClubMember(member)
                .stream()
                .map(this::map)
                .toList();

    }

    private ClubParticipationResponseDTO map(
            ClubParticipation participation){

        return ClubParticipationResponseDTO.builder()

                .id(participation.getId())

                .eventId(participation.getClubEvent().getId())

                .eventTitle(participation.getClubEvent().getTitle())

                .reservedPlaces(participation.getReservedPlaces())

                .status(participation.getStatus())

                .reservationDate(participation.getReservationDate())

                .build();

    }

}