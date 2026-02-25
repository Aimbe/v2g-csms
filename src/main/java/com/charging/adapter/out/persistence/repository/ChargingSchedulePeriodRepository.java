package com.charging.adapter.out.persistence.repository;

import com.charging.domain.entity.ChargingSchedulePeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChargingSchedulePeriodRepository extends JpaRepository<ChargingSchedulePeriod, Long> {
    List<ChargingSchedulePeriod> findByStationIdAndEvseId(String stationId, Integer evseId);

    void deleteByStationIdAndEvseId(String stationId, Integer evseId);
}
