package org.example.rawabet.cinema.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.rawabet.cinema.dto.request.CreateSalleRequest;
import org.example.rawabet.cinema.dto.response.SalleResponse;
import org.example.rawabet.cinema.repositories.SalleCinemaRepository;
import org.example.rawabet.cinema.services.interfaces.ISalleCinemaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/salles-cinema")
@RequiredArgsConstructor
public class SalleCinemaController {

    private final ISalleCinemaService salleService;
    private final SalleCinemaRepository salleCinemaRepository;

    @PostMapping
    @PreAuthorize("hasAuthority('CINEMA_UPDATE')")
    public SalleResponse createSalle(
            @Valid @RequestBody CreateSalleRequest request) {

        return salleService.createSalle(request);
    }

    @GetMapping("/cinema/{cinemaId}")
    public List<SalleResponse> getCinemaSalles(
            @PathVariable Long cinemaId) {

        return salleService.getCinemaSalles(cinemaId);
    }

    @GetMapping("/names")
    public List<Map<String, Object>> getSalleNames() {
        return salleCinemaRepository.findAll().stream()
                .filter(s -> s.getIsActive() == null || Boolean.TRUE.equals(s.getIsActive()))
                .map(s -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", s.getId());
                    map.put("name", s.getName());
                    return map;
                })
                .collect(Collectors.toList());
    }
    @GetMapping("/{id}")
    public SalleResponse getSalle(
            @PathVariable Long id) {

        return salleService.getSalleById(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CINEMA_UPDATE')")
    public void disableSalle(@PathVariable Long id) {

        salleService.disableSalle(id);
    }
}
