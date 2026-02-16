package com.charging.domain.port.out;

import com.charging.domain.entity.Evse;
import java.util.List;
import java.util.Optional;

public interface EvsePort {
    Evse save(Evse evse);
    Optional<Evse> findByEvseIdAndStationId(Integer evseId, String stationId);
    List<Evse> findByStationId(String stationId);
    Optional<Evse> findByEvseIdAndStationIdWithConnectors(Integer evseId, String stationId);
}
