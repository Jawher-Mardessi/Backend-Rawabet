package org.example.rawabet.cinema.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.cinema.dto.request.ConfigureHallRequest;
import org.example.rawabet.cinema.dto.response.SeatResponse;
import org.example.rawabet.cinema.dto.response.SeatRowResponse;
import org.example.rawabet.cinema.entities.SalleCinema;
import org.example.rawabet.cinema.entities.Seat;
import org.example.rawabet.cinema.entities.SeatRow;
import org.example.rawabet.cinema.enums.SeatType;
import org.example.rawabet.cinema.mappers.SeatMapper;
import org.example.rawabet.cinema.repositories.SalleCinemaRepository;
import org.example.rawabet.cinema.repositories.SeatRepository;
import org.example.rawabet.cinema.repositories.SeatRowRepository;
import org.example.rawabet.cinema.services.interfaces.ISeatService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements ISeatService {

    private final SalleCinemaRepository salleRepository;
    private final SeatRowRepository seatRowRepository;
    private final SeatRepository seatRepository;

    @Override
    @Transactional
    public void configureHall(ConfigureHallRequest request) {

        SalleCinema salle = salleRepository
                .findById(request.getSalleId())
                .orElseThrow(() -> new RuntimeException("Salle not found"));

        if (request.getRowConfigs() != null && !request.getRowConfigs().isEmpty()) {

            // ── Récupère les rangées existantes indexées par label ──
            Map<String, SeatRow> existingRows = seatRowRepository
                    .findBySalleIdOrderByDisplayOrder(salle.getId())
                    .stream()
                    .collect(Collectors.toMap(SeatRow::getRowLabel, r -> r));

            int totalCapacity = 0;

            for (int i = 0; i < request.getRowConfigs().size(); i++) {

                ConfigureHallRequest.RowConfig config = request.getRowConfigs().get(i);
                SeatType newType = config.getSeatType() != null ? config.getSeatType() : SeatType.STANDARD;

                if (existingRows.containsKey(config.getRowLabel())) {

                    // ── Rangée existante : vérifier si modifiée ──
                    SeatRow existingRow = existingRows.get(config.getRowLabel());
                    List<Seat> existingSeats = seatRepository.findByRowIdAndIsActiveTrue(existingRow.getId());

                    // Détecter le type actuel (majoritaire)
                    SeatType currentType = existingSeats.stream()
                            .map(Seat::getSeatType)
                            .findFirst()
                            .orElse(SeatType.STANDARD);

                    boolean countChanged = !existingRow.getSeatCount().equals(config.getSeatsPerRow());
                    boolean typeChanged  = !currentType.equals(newType);

                    if (countChanged || typeChanged) {
                        // Supprimer les sièges existants et recréer
                        seatRepository.deleteAll(existingSeats);
                        existingRow.setSeatCount(config.getSeatsPerRow());
                        existingRow.setDisplayOrder(i + 1);
                        SeatRow savedRow = seatRowRepository.save(existingRow);
                        generateSeats(savedRow, config.getSeatsPerRow(), newType);
                    }

                    existingRows.remove(config.getRowLabel());

                } else {

                    // ── Nouvelle rangée ──
                    SeatRow row = new SeatRow();
                    row.setRowLabel(config.getRowLabel());
                    row.setSeatCount(config.getSeatsPerRow());
                    row.setDisplayOrder(i + 1);
                    row.setSalle(salle);
                    SeatRow savedRow = seatRowRepository.save(row);
                    generateSeats(savedRow, config.getSeatsPerRow(), newType);
                }

                totalCapacity += config.getSeatsPerRow();
            }

            // ── Supprimer les rangées retirées de la config ──
            for (SeatRow removed : existingRows.values()) {
                seatRepository.deleteAll(seatRepository.findByRowIdAndIsActiveTrue(removed.getId()));
                seatRowRepository.delete(removed);
            }

            salle.setTotalCapacity(totalCapacity);
            salleRepository.save(salle);

        } else {

            // ── Ancien mode global (fallback) ──
            for (int i = 0; i < request.getNumberOfRows(); i++) {
                char rowLetter = (char) ('A' + i);
                SeatRow row = new SeatRow();
                row.setRowLabel(String.valueOf(rowLetter));
                row.setSeatCount(request.getSeatsPerRow());
                row.setDisplayOrder(i + 1);
                row.setSalle(salle);
                SeatRow savedRow = seatRowRepository.save(row);
                generateSeats(savedRow, request.getSeatsPerRow(), SeatType.STANDARD);
            }

            salle.setTotalCapacity(request.getNumberOfRows() * request.getSeatsPerRow());
            salleRepository.save(salle);
        }
    }

    @Override
    public List<SeatRowResponse> getRowsBySalle(Long salleId) {

        return seatRowRepository
                .findBySalleIdOrderByDisplayOrder(salleId)
                .stream()
                .map(row -> {
                    // Récupère le type dominant des sièges de la rangée
                    List<Seat> seats = seatRepository.findByRowIdAndIsActiveTrue(row.getId());
                    SeatType dominant = seats.stream()
                            .map(Seat::getSeatType)
                            .findFirst()
                            .orElse(SeatType.STANDARD);

                    return SeatRowResponse.builder()
                            .id(row.getId())
                            .rowLabel(row.getRowLabel())
                            .seatCount(row.getSeatCount())
                            .displayOrder(row.getDisplayOrder())
                            .dominantSeatType(dominant)
                            .build();
                })
                .toList();
    }

    private void generateSeats(SeatRow row, int seatsPerRow, SeatType seatType) {
        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= seatsPerRow; i++) {
            Seat seat = new Seat();
            String number = (i < 10) ? "0" + i : String.valueOf(i);
            seat.setFullLabel(row.getRowLabel() + "-" + number);
            seat.setSeatNumber(i);
            seat.setSeatType(seatType);
            seat.setIsActive(true);
            seat.setRow(row);
            seats.add(seat);
        }
        seatRepository.saveAll(seats);
    }

    @Override
    public List<SeatResponse> getSeatsBySalle(Long salleId) {
        return seatRepository
                .findByRowSalleId(salleId)
                .stream()
                .map(SeatMapper::toResponse)
                .toList();
    }

    @Override
    public void disableSeat(Long seatId) {
        Seat seat = seatRepository
                .findById(seatId)
                .orElseThrow(() -> new RuntimeException("Seat not found"));
        seat.setIsActive(false);
        seatRepository.save(seat);
    }
}