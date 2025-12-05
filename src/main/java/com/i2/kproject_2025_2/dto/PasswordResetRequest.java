package com.i2.kproject_2025_2.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordResetRequest(
        @Email @NotBlank String email,
        @NotBlank String code,
        @NotBlank @Size(min = 8, max = 64) String newPassword
) {}
