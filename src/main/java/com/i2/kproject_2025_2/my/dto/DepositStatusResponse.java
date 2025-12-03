package com.i2.kproject_2025_2.my.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DepositStatusResponse(
    @JsonProperty("user_id")
    Long userId,

    @JsonProperty("deposit_amount")
    int depositAmount,

    @JsonProperty("is_locked")
    boolean isLocked
) {}
