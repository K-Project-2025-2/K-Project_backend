package com.i2.kproject_2025_2.my.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserReportRequest {
    @JsonProperty("reported_user_id")
    private Long reportedUserId;
    private String reason;
    private String details;
}
