package com.i2.kproject_2025_2.shuttle.service;

import com.i2.kproject_2025_2.shuttle.dto.ShuttleRoute;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ShuttleService {

    public List<ShuttleRoute> getShuttleRoutes(boolean active) {
        // For now, return mock data. Later, this will be fetched from the database.
        ShuttleRoute routeA = new ShuttleRoute(1L, "A노선", "기흥역", "강남대학교 정문", Arrays.asList("기흥역", "강남대역", "기숙사", "정문"));
        ShuttleRoute routeB = new ShuttleRoute(2L, "B노선", "강남대학교", "기흥역", null);

        // The 'active' parameter is not used for now, but can be used for filtering later.
        return Arrays.asList(routeA, routeB);
    }
}
