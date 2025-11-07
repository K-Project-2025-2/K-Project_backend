package com.i2.kproject_2025_2.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String username,    // ✅ 아이디로 로그인
        @NotBlank String password
) {}