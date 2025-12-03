package com.i2.kproject_2025_2.my.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DepositPaymentResponse(
    String message,
    @JsonProperty("deposit_amount")
    int depositAmount
) {}
