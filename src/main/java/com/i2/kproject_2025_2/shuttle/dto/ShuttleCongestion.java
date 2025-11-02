package com.i2.kproject_2025_2.shuttle.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShuttleCongestion {
    @JsonProperty("bus_id")
    private String busId;

    @JsonProperty("congestion_level")
    private String congestionLevel;

    @JsonProperty("passenger_count")
    private int passengerCount;

    private int capacity;
}
