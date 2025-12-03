package com.i2.kproject_2025_2.taxi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SplitCreateRequest(
        @Schema(description = "총 택시비 (원)", example = "12000")
        @NotNull(message = "총 금액을 입력하세요.")
        @Min(value = 1, message = "총 금액은 1원 이상이어야 합니다.")
        Integer totalAmount
) {}
