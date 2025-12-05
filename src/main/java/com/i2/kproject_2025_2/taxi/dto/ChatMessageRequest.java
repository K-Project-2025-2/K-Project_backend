package com.i2.kproject_2025_2.taxi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatMessageRequest(
        @Schema(description = "메시지 본문", example = "학교 정문 앞에서 만나요.")
        @NotBlank(message = "메시지를 입력하세요.")
        @Size(max = 500, message = "메시지는 500자 이내여야 합니다.")
        String content
) {}

