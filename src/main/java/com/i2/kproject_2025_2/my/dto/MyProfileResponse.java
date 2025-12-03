package com.i2.kproject_2025_2.my.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MyProfileResponse(
    @JsonProperty("user_id")
    Long userId,
    String name,
    String studentId,
    String phone,
    String email
) {}
