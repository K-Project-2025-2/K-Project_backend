package com.i2.kproject_2025_2.taxi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ChatMessageResponse(
        @Schema(description = "메시지 ID", example = "1")
        Long id,
        @Schema(description = "방 코드", example = "123456")
        String roomCode,
        @Schema(description = "보낸 사람 사용자 ID", example = "42")
        Long senderId,
        @Schema(description = "보낸 사람 이메일", example = "user@example.com")
        String senderEmail,
        @Schema(description = "메시지 본문", example = "학교 정문 앞에서 만나요.")
        String content,
        @Schema(description = "전송 시각", example = "2025-03-01T18:20:00")
        LocalDateTime createdAt
) {}

