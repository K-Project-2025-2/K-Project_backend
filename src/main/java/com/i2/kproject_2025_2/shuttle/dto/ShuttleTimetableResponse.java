package com.i2.kproject_2025_2.shuttle.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShuttleTimetableResponse {
    private long route_id;
    private String date;
    private List<ShuttleTimetable> timetable;
}
