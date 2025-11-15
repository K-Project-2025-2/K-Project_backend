package com.i2.kproject_2025_2.my.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositStatusResponse {
    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("deposit_amount")
    private int depositAmount;

    @JsonProperty("is_locked")
    private boolean isLocked;
}
