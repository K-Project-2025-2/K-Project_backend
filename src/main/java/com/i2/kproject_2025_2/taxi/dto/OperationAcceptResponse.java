package com.i2.kproject_2025_2.taxi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record OperationAcceptResponse(
        @Schema(description = "방 코드", example = "ABC123")
        String roomCode,
        @Schema(description = "수락 여부", example = "true")
        boolean accepted,
        @Schema(description = "수락 시각", example = "2024-01-01T10:05:00")
        LocalDateTime acceptedAt,
        @Schema(description = "총 인원", example = "4")
        int totalMembers,
        @Schema(description = "수락 인원 수", example = "2")
        int acceptedCount,
        @Schema(description = "메시지", example = "운행 출발을 수락했습니다.")
        String message
) {}
