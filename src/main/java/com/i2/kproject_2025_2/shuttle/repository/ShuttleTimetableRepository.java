package com.i2.kproject_2025_2.shuttle.repository;

import com.i2.kproject_2025_2.shuttle.entity.ShuttleTimetable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShuttleTimetableRepository extends JpaRepository<ShuttleTimetable, Long> {

    // 특정 노선 ID에 해당하는 시간표 목록을 출발 시간 순으로 정렬하여 조회
    List<ShuttleTimetable> findByRouteIdOrderByDepartureTimeAsc(Long routeId);
}
