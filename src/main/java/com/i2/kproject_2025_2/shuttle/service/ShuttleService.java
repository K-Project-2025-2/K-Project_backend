package com.i2.kproject_2025_2.shuttle.service;

import com.i2.kproject_2025_2.entity.User;
import com.i2.kproject_2025_2.repository.UserRepository;
import com.i2.kproject_2025_2.shuttle.dto.*;
import com.i2.kproject_2025_2.shuttle.entity.FavoriteStation;
import com.i2.kproject_2025_2.shuttle.repository.FavoriteStationRepository;
import com.i2.kproject_2025_2.shuttle.repository.ShuttleRouteRepository;
import com.i2.kproject_2025_2.shuttle.repository.ShuttleTimetableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShuttleService {

    private final FavoriteStationRepository favoriteStationRepository;
    private final UserRepository userRepository;
    private final ShuttleRouteRepository shuttleRouteRepository;
    private final ShuttleTimetableRepository shuttleTimetableRepository;

    public List<ShuttleRoute> getShuttleRoutes(boolean active) {
        // 데이터베이스에서 모든 노선 정보를 조회합니다.
        List<com.i2.kproject_2025_2.shuttle.entity.ShuttleRoute> routeEntities = shuttleRouteRepository.findAll();

        // 엔티티 목록을 DTO 목록으로 변환합니다.
        return routeEntities.stream()
                .map(entity -> new ShuttleRoute(
                        entity.getId(),
                        entity.getName(),
                        entity.getOrigin(),
                        entity.getDestination(),
                        entity.getStations()
                ))
                .collect(Collectors.toList());
    }

    public ShuttleTimetableResponse getShuttleTimetable(long routeId, String date) {
        // TODO: date 파라미터를 사용한 요일별/날짜별 필터링 로직 추가 필요
        List<com.i2.kproject_2025_2.shuttle.entity.ShuttleTimetable> timetableEntities =
                shuttleTimetableRepository.findByRouteIdOrderByDepartureTimeAsc(routeId);

        // 엔티티 목록을 DTO 목록으로 변환
        List<ShuttleTimetable> timetable = timetableEntities.stream()
                .map(entity -> new ShuttleTimetable(
                        entity.getDepartureTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                        entity.getBusNumber()
                ))
                .collect(Collectors.toList());

        String responseDate = (date == null) ? LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) : date;

        return new ShuttleTimetableResponse(routeId, responseDate, timetable);
    }

    public List<ShuttleLocation> getShuttleLocations(Long routeId) {
        // TODO: 실시간 위치 정보 소스(e.g., WebSocket, 외부 API) 연동 필요
        return Collections.emptyList(); // 비어있는 리스트 반환
    }

    public ShuttleCongestionResponse getShuttleCongestion(Long routeId) {
        // TODO: 실시간 혼잡도 정보 소스 연동 필요
        List<ShuttleCongestion> congestionData = Collections.emptyList(); // 비어있는 리스트 반환
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
