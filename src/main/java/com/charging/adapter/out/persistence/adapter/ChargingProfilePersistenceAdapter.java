package com.charging.adapter.out.persistence.adapter;

import com.charging.adapter.out.persistence.repository.ChargingProfileRepository;
import com.charging.domain.entity.ChargingProfile;
import com.charging.domain.port.out.ChargingProfilePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ChargingProfilePersistenceAdapter implements ChargingProfilePort {

    private final ChargingProfileRepository chargingProfileRepository;

    @Override
    public ChargingProfile save(ChargingProfile chargingProfile) {
        return chargingProfileRepository.save(chargingProfile);
    }

    @Override
    public Optional<ChargingProfile> findByChargingProfileId(Integer chargingProfileId) {
        return chargingProfileRepository.findByChargingProfileId(chargingProfileId);
    }

    @Override
    public List<ChargingProfile> findByStationId(String stationId) {
        return chargingProfileRepository.findByStationId(stationId);
    }

    @Override
    public List<ChargingProfile> findActiveProfilesByStationId(String stationId, LocalDateTime now) {
        return chargingProfileRepository.findActiveProfilesByStationId(stationId, now);
    }
}
