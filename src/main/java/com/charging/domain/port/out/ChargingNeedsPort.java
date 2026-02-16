package com.charging.domain.port.out;

import com.charging.domain.entity.ChargingNeeds;

import java.util.List;
import java.util.Optional;

public interface ChargingNeedsPort {
    ChargingNeeds save(ChargingNeeds chargingNeeds);
    Optional<ChargingNeeds> findById(Long id);
    List<ChargingNeeds> findByStationIdAndEvseId(String stationId, Integer evseId);
    Optional<ChargingNeeds> findLatestByStationIdAndEvseId(String stationId, Integer evseId);
}
