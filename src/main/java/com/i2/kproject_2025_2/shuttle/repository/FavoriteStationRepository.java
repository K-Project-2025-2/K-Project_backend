package com.i2.kproject_2025_2.shuttle.repository;

import com.i2.kproject_2025_2.shuttle.entity.FavoriteStation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteStationRepository extends JpaRepository<FavoriteStation, Long> {
    List<FavoriteStation> findByUser_Id(Long userId);
}
