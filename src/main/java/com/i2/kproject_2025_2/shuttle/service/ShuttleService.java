package com.i2.kproject_2025_2.shuttle.service;

import com.i2.kproject_2025_2.entity.User;
import com.i2.kproject_2025_2.repository.UserRepository;
import com.i2.kproject_2025_2.shuttle.dto.*;
import com.i2.kproject_2025_2.shuttle.entity.FavoriteStation;
import com.i2.kproject_2025_2.shuttle.repository.FavoriteStationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShuttleService {

    private final FavoriteStationRepository favoriteStationRepository;
    private final UserRepository userRepository;

    public List<ShuttleRoute> getShuttleRoutes(boolean active) {
        // For now, return mock data. Later, this will be fetched from the database.
        ShuttleRoute routeA = new ShuttleRoute(1L, "A노선", "기흥역", "강남대학교 정문", Arrays.asList("기흥역", "강남대역", "기숙사", "정문"));
        ShuttleRoute routeB = new ShuttleRoute(2L, "B노선", "강남대학교", "기흥역", null);

        // The 'active' parameter is not used for now, but can be used for filtering later.
        return Arrays.asList(routeA, routeB);
    }

    public ShuttleTimetableResponse getShuttleTimetable(long routeId, String date) {
        // For now, return mock data. Later, this will be fetched from the database.
        List<ShuttleTimetable> timetable = Arrays.asList(
                new ShuttleTimetable("08:00", "DG01"),
                new ShuttleTimetable("08:20", "DG02"),
                new ShuttleTimetable("08:40", "DG03")
        );

        String responseDate = (date == null) ? LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) : date;

        return new ShuttleTimetableResponse(routeId, responseDate, timetable);
    }

    public List<ShuttleLocation> getShuttleLocations(Long routeId) {
        // For now, return mock data. Later, this will be fetched from a real-time data source.
        List<ShuttleLocation> allLocations = Arrays.asList(
                new ShuttleLocation("DG01", 1L, 37.275391, 127.115723, 23.5, OffsetDateTime.now(ZoneOffset.UTC), "강남대역", "기숙사"),
                new ShuttleLocation("DG02", 1L, 37.276211, 127.118302, 0.0, OffsetDateTime.now(ZoneOffset.UTC).plusSeconds(10), "정문", null),
                new ShuttleLocation("DG03", 2L, 37.275391, 127.115723, 40.0, OffsetDateTime.now(ZoneOffset.UTC), "강남대역", "기흥역")
        );

        if (routeId == null) {
            return allLocations;
        }

        return allLocations.stream()
                .filter(location -> location.getRouteId() == routeId)
                .collect(Collectors.toList());
    }

    public ShuttleCongestionResponse getShuttleCongestion(Long routeId) {
        // For now, return mock data. Later, this will be fetched from a real-time data source.
        List<ShuttleCongestion> congestionData = Arrays.asList(
                new ShuttleCongestion("DG01", "MEDIUM", 18, 30),
                new ShuttleCongestion("DG02", "HIGH", 28, 30)
        );

        return new ShuttleCongestionResponse(routeId, congestionData);
    }

    @Transactional(readOnly = true)
    public FavoriteStationListResponse getFavoriteStations(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        List<FavoriteStationDto> favorites = favoriteStationRepository.findByUser_Id(user.getId())
                .stream()
                .map(fav -> new FavoriteStationDto(fav.getId(), fav.getStation()))
                .collect(Collectors.toList());

        return new FavoriteStationListResponse(favorites);
    }

    @Transactional
    public void addFavoriteStation(String email, String station) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        FavoriteStation favoriteStation = new FavoriteStation();
        favoriteStation.setUser(user);
        favoriteStation.setStation(station);
        favoriteStationRepository.save(favoriteStation);
    }

    @Transactional
    public void removeFavoriteStation(String email, Long favoriteId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        FavoriteStation favorite = favoriteStationRepository.findById(favoriteId)
                .orElseThrow(() -> new RuntimeException("Favorite not found"));

        if (!favorite.getUser().getId().equals(user.getId())) {
            throw new SecurityException("You do not have permission to delete this favorite");
        }

        favoriteStationRepository.delete(favorite);
    }
}
