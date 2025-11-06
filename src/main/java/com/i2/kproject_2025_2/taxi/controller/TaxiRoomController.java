package com.i2.kproject_2025_2.taxi.controller;

import com.i2.kproject_2025_2.taxi.dto.CreateRoomRequest;
import com.i2.kproject_2025_2.taxi.dto.JoinRoomRequest;
import com.i2.kproject_2025_2.taxi.dto.RoomResponse;
import com.i2.kproject_2025_2.taxi.service.TaxiRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/taxi/rooms")
@RequiredArgsConstructor
public class TaxiRoomController {

    private final TaxiRoomService taxiRoomService;

    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(Principal principal,
                                                   @RequestBody CreateRoomRequest req) {
        RoomResponse res = taxiRoomService.createRoom(principal.getName(), req);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/join")
    public ResponseEntity<RoomResponse> joinRoom(Principal principal,
                                                 @RequestBody JoinRoomRequest req) {
        RoomResponse res = taxiRoomService.joinRoom(principal.getName(), req);
        return ResponseEntity.ok(res);
    }
}

