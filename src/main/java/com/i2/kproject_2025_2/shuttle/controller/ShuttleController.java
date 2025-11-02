package com.i2.kproject_2025_2.shuttle.controller;

import com.i2.kproject_2025_2.shuttle.dto.ShuttleCongestionResponse;
import com.i2.kproject_2025_2.shuttle.dto.ShuttleLocationListResponse;
import com.i2.kproject_2025_2.shuttle.dto.ShuttleRouteListResponse;
import com.i2.kproject_2025_2.shuttle.dto.ShuttleTimetableResponse;
import com.i2.kproject_2025_2.shuttle.service.ShuttleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
