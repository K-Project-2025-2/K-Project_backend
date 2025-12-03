package com.i2.kproject_2025_2.taxi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateRoomRequest(
        @Schema(description = "만날 장소", example = "기흥역")
        @NotBlank(message = "탑승 위치를 입력하세요.")
        @Size(max = 255, message = "탑승 위치는 255자 이내여야 합니다.")
        String meetingPoint,

        @Schema(description = "도착지", example = "이공관")
        @NotBlank(message = "도착지를 입력하세요.")
        @Size(max = 255, message = "도착지는 255자 이내여야 합니다.")
        String destination,

        @Schema(description = "만남 시간 (ISO 8601)", example = "2025-03-01T18:30:00")
        @NotNull(message = "만남 시간을 입력하세요.")
        LocalDateTime meetingTime,

        @Schema(description = "정원 (2~4명)", example = "3")
        @NotNull(message = "정원을 입력하세요.")
        @Min(value = 2, message = "정원은 2명 이상이어야 합니다.")
        @Max(value = 4, message = "정원은 4명 이하여야 합니다.")
        Integer capacity
) {}

