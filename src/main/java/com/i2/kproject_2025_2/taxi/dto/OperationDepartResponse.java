package com.i2.kproject_2025_2.taxi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record OperationDepartResponse(
        @Schema(description = "방 코드", example = "ABC123")
        String roomCode,
        @Schema(description = "운행 상태", example = "DEPARTED")
        String operationStatus,
        @Schema(description = "출발 확정 시각", example = "2024-01-01T10:10:00")
        LocalDateTime departedAt,
        @Schema(description = "메시지", example = "운행이 출발했습니다.")
        String message
) {}
