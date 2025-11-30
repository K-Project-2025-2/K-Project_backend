package com.i2.kproject_2025_2.taxi.dto;

import java.time.LocalDateTime;

public record ChatMessageResponse(
        Long id,
        String roomCode,
        Long senderId,
        String senderEmail,
        String content,
        LocalDateTime createdAt
) {}

