package org.example.rawabet.controllers;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.entities.ActivityLog;
import org.example.rawabet.repositories.ActivityLogRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
@RequestMapping("/admin/activity")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogRepository repo;

    // ── GET /admin/activity?hours=6&type=all ──────────────────────────
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    public ResponseEntity<Map<String, Object>> getActivity(
            @RequestParam(defaultValue = "6")    int    hours,
            @RequestParam(required = false)       String from,
            @RequestParam(required = false)       String to,
            @RequestParam(defaultValue = "all")   String type) {

        Instant fromInstant;
        Instant toInstant;

        if (from != null && !from.isBlank()) {
            fromInstant = Instant.parse(from);
            toInstant   = (to != null && !to.isBlank()) ? Instant.parse(to) : Instant.now();
        } else {
            toInstant   = Instant.now();
            fromInstant = toInstant.minus(hours, ChronoUnit.HOURS);
        }

        List<ActivityLog> activities;

        // Map type → event types
        Map<String, List<String>> typeMap = Map.of(
                "login",    List.of("user_login", "face_login"),
                "register", List.of("user_register"),
                "suspect",  List.of("suspect_login"),
                "ban",      List.of("user_ban", "user_unban"),
                "loyalty",  List.of("loyalty_upgrade", "loyalty_points")
        );

        if ("all".equals(type)) {
            activities = repo.findByTimestampBetweenOrderByTimestampDesc(fromInstant, toInstant);
        } else if (typeMap.containsKey(type)) {
            activities = repo.findByTypesAndPeriod(typeMap.get(type), fromInstant, toInstant);
        } else {
            activities = repo.findByTimestampBetweenOrderByTimestampDesc(fromInstant, toInstant);
        }

        // Stats par type
        Map<String, Long> stats = new LinkedHashMap<>();
        for (var entry : typeMap.entrySet()) {
            long count = activities.stream()
                    .filter(a -> entry.getValue().contains(a.getType()))
                    .count();
            stats.put(entry.getKey(), count);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("activities", activities);
        response.put("total",      activities.size());
        response.put("stats",      stats);
        response.put("from",       fromInstant.toString());
        response.put("to",         toInstant.toString());

        return ResponseEntity.ok(response);
    }
}