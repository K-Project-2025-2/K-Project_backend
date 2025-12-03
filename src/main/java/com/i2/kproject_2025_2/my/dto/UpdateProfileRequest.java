package com.i2.kproject_2025_2.my.dto;

public record UpdateProfileRequest(
    String name,
    String phone,
    String studentId // 학번 필드 추가
) {}
