package com.i2.kproject_2025_2.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
    int statusCode,
    String error,
    String message,
    LocalDateTime timestamp
) {}
