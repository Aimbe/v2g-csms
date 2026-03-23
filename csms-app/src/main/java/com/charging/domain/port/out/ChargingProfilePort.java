package com.charging.domain.port.out;

import com.charging.domain.entity.ChargingProfile;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChargingProfilePort {
    ChargingProfile save(ChargingProfile chargingProfile);
    Optional<ChargingProfile> findByChargingProfileId(Integer chargingProfileId);
    List<ChargingProfile> findByStationId(String stationId);
    List<ChargingProfile> findActiveProfilesByStationId(String stationId, LocalDateTime now);
}
