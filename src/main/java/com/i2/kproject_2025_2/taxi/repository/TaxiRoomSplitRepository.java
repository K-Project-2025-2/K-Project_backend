package com.i2.kproject_2025_2.taxi.repository;

import com.i2.kproject_2025_2.taxi.entity.TaxiRoom;
import com.i2.kproject_2025_2.taxi.entity.TaxiRoomSplit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaxiRoomSplitRepository extends JpaRepository<TaxiRoomSplit, Long> {
    Optional<TaxiRoomSplit> findByRoom(TaxiRoom room);
    boolean existsByRoom(TaxiRoom room);
}
