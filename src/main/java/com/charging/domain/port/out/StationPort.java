package com.charging.domain.port.out;

import com.charging.domain.entity.Station;
import java.util.Optional;

public interface StationPort {
    Station save(Station station);
    Optional<Station> findByStationId(String stationId);
    boolean existsByStationId(String stationId);
    Optional<Station> findByStationIdWithEvses(String stationId);
}
