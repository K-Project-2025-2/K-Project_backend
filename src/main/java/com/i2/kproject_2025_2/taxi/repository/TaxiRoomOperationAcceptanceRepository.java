package com.i2.kproject_2025_2.taxi.repository;

import com.i2.kproject_2025_2.taxi.entity.TaxiRoom;
import com.i2.kproject_2025_2.taxi.entity.TaxiRoomOperationAcceptance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaxiRoomOperationAcceptanceRepository extends JpaRepository<TaxiRoomOperationAcceptance, Long> {
    boolean existsByRoom_IdAndUser_Id(Long roomId, Long userId);
    long countByRoom_Id(Long roomId);
    Optional<TaxiRoomOperationAcceptance> findByRoom_IdAndUser_Id(Long roomId, Long userId);
    List<TaxiRoomOperationAcceptance> findByRoom(TaxiRoom room);
    void deleteByRoom_Id(Long roomId);
}
