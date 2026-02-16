package com.charging.adapter.out.persistence.adapter;

import com.charging.adapter.out.persistence.repository.StationRepository;
import com.charging.domain.entity.Station;
import com.charging.domain.port.out.StationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StationPersistenceAdapter implements StationPort {

    private final StationRepository stationRepository;

    @Override
    public Station save(Station station) {
        return stationRepository.save(station);
    }

    @Override
    public Optional<Station> findByStationId(String stationId) {
        return stationRepository.findByStationId(stationId);
    }

    @Override
    public boolean existsByStationId(String stationId) {
        return stationRepository.existsByStationId(stationId);
    }

    @Override
    public Optional<Station> findByStationIdWithEvses(String stationId) {
        return stationRepository.findByStationIdWithEvses(stationId);
    }
}
