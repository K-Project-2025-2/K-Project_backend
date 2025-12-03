package com.i2.kproject_2025_2.taxi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record SplitResponse(
        @Schema(description = "방 코드", example = "ABC123")
        String roomCode,
        @Schema(description = "정산 ID", example = "1")
        Long splitId,
        @Schema(description = "총 금액", example = "12000")
        int totalAmount,
        @Schema(description = "참여 인원 수", example = "4")
        int memberCount,
        @Schema(description = "1인당 금액", example = "3000")
        int amountPerPerson,
        @Schema(description = "개인별 금액 분배 (userId -> 금액)")
        Map<String, Integer> individualCosts,
        @Schema(description = "송금 완료한 사용자 ID 목록", example = "[1,2]")
        List<Long> paidMembers,
        @Schema(description = "정산 생성 시각", example = "2024-01-01T10:30:00")
        LocalDateTime createdAt,
        @Schema(description = "정산 상태 (PENDING, COMPLETED)", example = "PENDING")
        String status
) {}
