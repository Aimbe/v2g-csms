package com.charging.domain.port.in;

import com.charging.domain.entity.Station;
import java.util.List;

public interface StationQueryUseCase {
    List<Station> getAllStations();
    Station getStationDetail(String stationId);
}
