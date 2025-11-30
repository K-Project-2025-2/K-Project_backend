package com.i2.kproject_2025_2.taxi.repository;

import com.i2.kproject_2025_2.taxi.entity.TaxiRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaxiRoomMemberRepository extends JpaRepository<TaxiRoomMember, Long> {
    boolean existsByRoom_IdAndUser_Id(Long roomId, Long userId);
    long countByRoom_Id(Long roomId);
    java.util.Optional<TaxiRoomMember> findByRoom_IdAndUser_Id(Long roomId, Long userId);
}
