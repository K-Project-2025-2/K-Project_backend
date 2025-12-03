package com.i2.kproject_2025_2.taxi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record RoomResponse(
        @Schema(description = "ID", example = "10")
        Long id,
        @Schema(description = "방 코드", example = "123456")
        String roomCode,
        @Schema(description = "만날 장소", example = "기흥역")
        String meetingPoint,
        @Schema(description = "도착지", example = "이공관")
        String destination,
        @Schema(description = "만남 시간", example = "2025-03-01T18:30:00")
        LocalDateTime meetingTime,
        @Schema(description = "정원 (2~4명)", example = "3")
        int capacity,
        @Schema(description = "상태 (OPEN, FULL)", example = "OPEN")
        String status,
        @Schema(description = "현재 인원 수", example = "2")
        int memberCount,
        @Schema(description = "방장 사용자 ID", example = "abc@kangnam.ac.kr")
        Long leaderId
) {}

