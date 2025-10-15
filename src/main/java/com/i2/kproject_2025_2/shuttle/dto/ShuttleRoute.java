package com.i2.kproject_2025_2.shuttle.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShuttleRoute {
    private long id;
    private String name;
    private String start_point;
    private String end_point;
    private List<String> stations;
}
