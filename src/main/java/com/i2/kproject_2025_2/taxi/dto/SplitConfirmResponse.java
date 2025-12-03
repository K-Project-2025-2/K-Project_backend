package com.i2.kproject_2025_2.taxi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record SplitConfirmResponse(
        @Schema(description = "방 코드", example = "ABC123")
        String roomCode,
        @Schema(description = "정산 ID", example = "1")
        Long splitId,
        @Schema(description = "사용자 ID", example = "3")
        Long userId,
        @Schema(description = "송금 완료 시각", example = "2024-01-01T10:35:00")
        LocalDateTime paidAt,
        @Schema(description = "모두 송금 완료 여부", example = "false")
        boolean allPaid,
        @Schema(description = "송금 완료 인원", example = "3")
        int paidCount,
        @Schema(description = "총 인원", example = "4")
        int totalMembers,
        @Schema(description = "메시지", example = "송금 완료가 확인되었습니다.")
        String message
) {}
