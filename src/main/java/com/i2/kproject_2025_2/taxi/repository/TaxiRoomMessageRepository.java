package com.i2.kproject_2025_2.taxi.repository;

import com.i2.kproject_2025_2.taxi.entity.TaxiRoomMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaxiRoomMessageRepository extends JpaRepository<TaxiRoomMessage, Long> {
    List<TaxiRoomMessage> findByRoom_RoomCodeOrderByCreatedAtDesc(String roomCode, Pageable pageable);
}

