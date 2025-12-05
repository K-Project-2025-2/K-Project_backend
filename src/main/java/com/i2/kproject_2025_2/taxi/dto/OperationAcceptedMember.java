package com.i2.kproject_2025_2.taxi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record OperationAcceptedMember(
        @Schema(description = "사용자 ID", example = "1")
        Long userId,
        @Schema(description = "사용자 이름", example = "홍길동")
        String userName,
        @Schema(description = "수락 시각", example = "2024-01-01T10:05:00")
        LocalDateTime acceptedAt
) {}
