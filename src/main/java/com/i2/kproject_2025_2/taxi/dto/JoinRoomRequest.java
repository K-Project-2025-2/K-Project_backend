package com.i2.kproject_2025_2.taxi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record JoinRoomRequest(
        @Schema(description = "택시 방 코드", example = "123456")
        @NotBlank(message = "방 코드를 입력하세요.")
        @Size(min = 6, max = 6, message = "방 코드는 6자리여야 합니다.")
        @Pattern(regexp = "\\d{6}", message = "방 코드는 숫자 6자리여야 합니다.")
        String roomCode
) {}

