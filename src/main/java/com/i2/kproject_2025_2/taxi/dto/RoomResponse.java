package com.i2.kproject_2025_2.taxi.dto;

import java.time.LocalDateTime;

public record RoomResponse(
        Long id,
        String roomCode,
        String meetingPoint,
        String destination,
        LocalDateTime meetingTime,
        int capacity,
        String status,
        int memberCount,
        Long leaderId
) {}

