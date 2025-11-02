package com.i2.kproject_2025_2.shuttle.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShuttleLocation {
    @JsonProperty("bus_id")
    private String busId;

    @JsonProperty("route_id")
    private long routeId;

    private double latitude;
    private double longitude;
    private double speed;

    @JsonProperty("last_updated")
    private OffsetDateTime lastUpdated;

    @JsonProperty("current_station")
    private String currentStation;

    @JsonProperty("next_station")
    private String nextStation;
}
