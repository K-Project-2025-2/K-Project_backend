package com.i2.kproject_2025_2.lostandfound.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LostItemReportResponse {
    private String message;

    @JsonProperty("report_id")
    private Long reportId;
}
