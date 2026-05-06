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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClubParticipationServiceImpl implements IClubParticipationService {

    private final ClubParticipationRepository participationRepository;
    private final ClubEventRepository eventRepository;
    private final ClubMemberRepository memberRepository;
    private final IAuthService authService;

    @Override
    @Transactional
    public ClubParticipationResponseDTO reserve(ClubParticipationRequestDTO dto) {

        User user = authService.getAuthenticatedUser();

        ClubMember member = memberRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException("Not a club member"));

        if (member.getStatus() != ClubMemberStatus.ACTIVE) {
            throw new BusinessException("Inactive member");
        }

        // ✅ BUG 3 CORRIGÉ : verrou pessimiste pour éviter la race condition
        ClubEvent event = eventRepository.findByIdWithLock(dto.getEventId())
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Event finished");
        }

        if (event.getReservedPlaces() + dto.getPlaces() > event.getMaxPlaces()) {
            throw new BusinessException("Not enough places");
        }

        // ✅ BUG 4 CORRIGÉ : réactiver la participation annulée au lieu d'en créer une nouvelle
        Optional<ClubParticipation> existing =
                participationRepository.findByClubMemberAndClubEvent(member, event);

        if (existing.isPresent()) {
            ClubParticipation p = existing.get();

            if (p.getStatus() == ClubParticipationStatus.CONFIRMED) {
                throw new BusinessException("Already reserved");
            }

            // Réactivation de la participation annulée
            event.setReservedPlaces(event.getReservedPlaces() + dto.getPlaces());
            p.setReservedPlaces(dto.getPlaces());
            p.setStatus(ClubParticipationStatus.CONFIRMED);
            p.setReservationDate(LocalDateTime.now());
            return map(participationRepository.save(p));
        }

        // Première réservation
        event.setReservedPlaces(event.getReservedPlaces() + dto.getPlaces());

        ClubParticipation participation = ClubParticipation.builder()
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
    public ClubParticipationResponseDTO updateReservation(Long id, int newPlaces) {

        User user = authService.getAuthenticatedUser();

        ClubParticipation participation = participationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation not found"));

        if (!participation.getClubMember().getUser().getId().equals(user.getId())) {
            throw new BusinessException("Not your reservation");
        }

        if (participation.getStatus() != ClubParticipationStatus.CONFIRMED) {
            throw new BusinessException("Reservation is not active");
        }

        ClubEvent event = participation.getClubEvent();

        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Event already finished");
        }

        int diff = newPlaces - participation.getReservedPlaces();

        if (event.getReservedPlaces() + diff > event.getMaxPlaces()) {
            throw new BusinessException("Not enough places available");
        }

        event.setReservedPlaces(event.getReservedPlaces() + diff);
        participation.setReservedPlaces(newPlaces);

        return map(participationRepository.save(participation));
    }

    @Override
    @Transactional
    public void cancel(Long id) {

        User user = authService.getAuthenticatedUser();

        ClubParticipation participation = participationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation not found"));

        if (!participation.getClubMember().getUser().getId().equals(user.getId())) {
            throw new BusinessException("Not your reservation");
        }

        if (participation.getStatus() == ClubParticipationStatus.CANCELLED) {
            return;
        }

        ClubEvent event = participation.getClubEvent();

        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Event already finished");
        }

        event.setReservedPlaces(
                Math.max(0, event.getReservedPlaces() - participation.getReservedPlaces())
        );

        participation.setStatus(ClubParticipationStatus.CANCELLED);
        participationRepository.save(participation);
    }

    @Override
    public List<ClubParticipationResponseDTO> myReservations() {

        User user = authService.getAuthenticatedUser();

        ClubMember member = memberRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException("Not member"));

        return participationRepository.findByClubMember(member)
                .stream()
                .map(this::map)
                .toList();
    }

    private ClubParticipationResponseDTO map(ClubParticipation participation) {
        return ClubParticipationResponseDTO.builder()
                .id(participation.getId())
                .eventId(participation.getClubEvent().getId())
                .eventTitle(participation.getClubEvent().getTitle())
                .reservedPlaces(participation.getReservedPlaces())
                .remainingPlaces(participation.getClubEvent().getRemainingPlaces())
                .status(participation.getStatus())
                .reservationDate(participation.getReservationDate())
                .build();
    }
}