package com.i2.kproject_2025_2.shuttle.controller;

import com.i2.kproject_2025_2.shuttle.dto.*;
import com.i2.kproject_2025_2.shuttle.service.ShuttleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shuttle")
@RequiredArgsConstructor
public class ShuttleController {

    private final ShuttleService shuttleService;

    @GetMapping("/routes")
    public ResponseEntity<ShuttleRouteListResponse> getShuttleRoutes(@RequestParam(required = false) boolean active) {
        ShuttleRouteListResponse response = new ShuttleRouteListResponse(shuttleService.getShuttleRoutes(active));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/timetable")
    public ResponseEntity<ShuttleTimetableResponse> getShuttleTimetable(@RequestParam long routeId, @RequestParam(required = false) String date) {
        ShuttleTimetableResponse response = shuttleService.getShuttleTimetable(routeId, date);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/locations")
    public ResponseEntity<ShuttleLocationListResponse> getShuttleLocations(@RequestParam(required = false) Long routeId) {
        ShuttleLocationListResponse response = new ShuttleLocationListResponse(shuttleService.getShuttleLocations(routeId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/congestion")
    public ResponseEntity<ShuttleCongestionResponse> getShuttleCongestion(@RequestParam(required = false) Long routeId) {
        ShuttleCongestionResponse response = shuttleService.getShuttleCongestion(routeId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/favorites")
    public ResponseEntity<FavoriteStationListResponse> getFavoriteStations(@AuthenticationPrincipal String email) {
        FavoriteStationListResponse response = shuttleService.getFavoriteStations(email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/favorites")
    public ResponseEntity<MessageResponse> addFavoriteStation(@AuthenticationPrincipal String email, @RequestBody AddFavoriteRequest req) {
        shuttleService.addFavoriteStation(email, req.getStation());
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Added to favorites"));
    }

    @DeleteMapping("/favorites/{id}")
    public ResponseEntity<MessageResponse> removeFavoriteStation(@AuthenticationPrincipal String email, @PathVariable Long id) {
        shuttleService.removeFavoriteStation(email, id);
        return ResponseEntity.ok(new MessageResponse("Removed from favorites"));
    }
}
