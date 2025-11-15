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
public class MyProfileResponse {
    @JsonProperty("user_id")
    private Long userId;
    private String name;
    private String studentId;
    private String phone;
    private String email;
}
