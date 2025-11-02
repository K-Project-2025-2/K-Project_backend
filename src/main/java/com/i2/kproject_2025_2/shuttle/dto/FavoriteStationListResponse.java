package com.i2.kproject_2025_2.shuttle.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteStationListResponse {
    private List<FavoriteStationDto> favorites;
}
