package com.i2.kproject_2025_2.shuttle.controller;

import com.i2.kproject_2025_2.shuttle.dto.*;
import com.i2.kproject_2025_2.shuttle.service.ShuttleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shuttle")
@RequiredArgsConstructor
public class ShuttleController {

    private final ShuttleService shuttleService;

    // ... existing methods ...

    @GetMapping("/favorites")
    public ResponseEntity<FavoriteStationListResponse> getFavoriteStations(@AuthenticationPrincipal UserDetails userDetails) {
        FavoriteStationListResponse response = shuttleService.getFavoriteStations(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/favorites")
    public ResponseEntity<MessageResponse> addFavoriteStation(@AuthenticationPrincipal UserDetails userDetails, @RequestBody AddFavoriteRequest req) {
        shuttleService.addFavoriteStation(userDetails.getUsername(), req.getStation());
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Added to favorites"));
    }

    @DeleteMapping("/favorites/{id}")
    public ResponseEntity<MessageResponse> removeFavoriteStation(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        shuttleService.removeFavoriteStation(userDetails.getUsername(), id);
        return ResponseEntity.ok(new MessageResponse("Removed from favorites"));
    }
}
