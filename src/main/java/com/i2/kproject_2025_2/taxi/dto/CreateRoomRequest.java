package com.i2.kproject_2025_2.taxi.dto;

import java.time.LocalDateTime;

public record CreateRoomRequest(
        String meetingPoint,
        String destination,
        LocalDateTime meetingTime,
        Integer capacity
) {}

