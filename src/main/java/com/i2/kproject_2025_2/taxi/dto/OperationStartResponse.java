package com.i2.kproject_2025_2.taxi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record OperationStartResponse(
        @Schema(description = "방 코드", example = "ABC123")
        String roomCode,
        @Schema(description = "운행 상태", example = "STARTED")
        String operationStatus,
        @Schema(description = "운행 시작 시각", example = "2024-01-01T10:00:00")
        LocalDateTime startedAt,
        @Schema(description = "메시지", example = "운행이 시작되었습니다.")
        String message
) {}
