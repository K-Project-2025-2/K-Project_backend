package com.i2.kproject_2025_2.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String email,    // ✅ 이메일로 로그인
        @NotBlank String password
) {}
