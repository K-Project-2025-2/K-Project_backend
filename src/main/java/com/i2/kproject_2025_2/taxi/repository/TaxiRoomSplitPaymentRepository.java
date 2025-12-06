package com.i2.kproject_2025_2.taxi.repository;

import com.i2.kproject_2025_2.taxi.entity.TaxiRoomSplit;
import com.i2.kproject_2025_2.taxi.entity.TaxiRoomSplitPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaxiRoomSplitPaymentRepository extends JpaRepository<TaxiRoomSplitPayment, Long> {
    boolean existsBySplit_IdAndUser_Id(Long splitId, Long userId);
    long countBySplit_Id(Long splitId);
    List<TaxiRoomSplitPayment> findBySplit(TaxiRoomSplit split);
    Optional<TaxiRoomSplitPayment> findBySplit_IdAndUser_Id(Long splitId, Long userId);
    void deleteBySplit(TaxiRoomSplit split);
}
