package com.i2.kproject_2025_2.taxi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record OperationStatusResponse(
        @Schema(description = "방 코드", example = "ABC123")
        String roomCode,
        @Schema(description = "운행 상태", example = "STARTED")
        String operationStatus,
        @Schema(description = "운행 시작 시각", example = "2024-01-01T10:00:00")
        LocalDateTime startedAt,
        @Schema(description = "출발 확정 시각", example = "2024-01-01T10:10:00")
        LocalDateTime departedAt,
        @Schema(description = "운행 종료 시각", example = "2024-01-01T10:30:00")
        LocalDateTime endedAt,
        @Schema(description = "총 인원", example = "4")
        int totalMembers,
        @Schema(description = "수락한 멤버 목록")
        List<OperationAcceptedMember> acceptedMembers,
        @Schema(description = "수락 인원 수", example = "2")
        int acceptedCount,
        @Schema(description = "모두 수락했는지 여부", example = "false")
        boolean isAllAccepted
) {}
