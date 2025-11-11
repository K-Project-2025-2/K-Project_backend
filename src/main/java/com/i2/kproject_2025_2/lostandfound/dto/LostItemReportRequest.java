package com.i2.kproject_2025_2.lostandfound.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LostItemReportRequest {
    private String location;
    private String description;
}
