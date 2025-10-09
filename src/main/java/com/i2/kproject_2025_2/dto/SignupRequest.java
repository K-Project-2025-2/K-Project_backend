package com.i2.kproject_2025_2.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank @Size(min = 4, max = 20) String username,   // ✅ 아이디
        @Email @NotBlank String email,                         // ✅ 학교 메일
        @NotBlank @Size(min = 8, max = 64) String password
) {}