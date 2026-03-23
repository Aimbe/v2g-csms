package com.charging.domain.port.out;

import com.charging.domain.entity.ChargingSchedulePeriod;

import java.util.List;

public interface ChargingSchedulePeriodPort {
    ChargingSchedulePeriod save(ChargingSchedulePeriod period);
    List<ChargingSchedulePeriod> saveAll(List<ChargingSchedulePeriod> periods);
    List<ChargingSchedulePeriod> findByStationIdAndEvseId(String stationId, Integer evseId);
    void deleteByStationIdAndEvseId(String stationId, Integer evseId);
}
