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

    // ... existing methods ...

    @Transactional(readOnly = true)
    public FavoriteStationListResponse getFavoriteStations(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<FavoriteStationDto> favorites = favoriteStationRepository.findByUser_Id(user.getId())
                .stream()
                .map(fav -> new FavoriteStationDto(fav.getId(), fav.getStation()))
                .collect(Collectors.toList());

        return new FavoriteStationListResponse(favorites);
    }

    @Transactional
    public void addFavoriteStation(String username, String station) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        FavoriteStation favoriteStation = new FavoriteStation();
        favoriteStation.setUser(user);
        favoriteStation.setStation(station);
        favoriteStationRepository.save(favoriteStation);
    }

    @Transactional
    public void removeFavoriteStation(String username, Long favoriteId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        FavoriteStation favorite = favoriteStationRepository.findById(favoriteId)
                .orElseThrow(() -> new RuntimeException("Favorite not found"));

        if (!favorite.getUser().getId().equals(user.getId())) {
            throw new SecurityException("You do not have permission to delete this favorite");
        }

        favoriteStationRepository.delete(favorite);
    }
}
