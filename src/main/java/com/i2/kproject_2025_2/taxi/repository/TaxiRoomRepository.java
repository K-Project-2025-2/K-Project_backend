package com.i2.kproject_2025_2.taxi.repository;

import com.i2.kproject_2025_2.taxi.entity.TaxiRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaxiRoomRepository extends JpaRepository<TaxiRoom, Long> {
    Optional<TaxiRoom> findByRoomCode(String roomCode);
    boolean existsByRoomCode(String roomCode);
}

