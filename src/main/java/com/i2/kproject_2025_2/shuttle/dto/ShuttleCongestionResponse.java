package com.i2.kproject_2025_2.shuttle.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShuttleCongestionResponse {
    @JsonProperty("route_id")
    private Long routeId;

    private List<ShuttleCongestion> buses;
}
