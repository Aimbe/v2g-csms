package com.charging.adapter.out.persistence.adapter;

import com.charging.adapter.out.persistence.repository.EvseRepository;
import com.charging.domain.entity.Evse;
import com.charging.domain.port.out.EvsePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EvsePersistenceAdapter implements EvsePort {

    private final EvseRepository evseRepository;

    @Override
    public Evse save(Evse evse) {
        return evseRepository.save(evse);
    }

    @Override
    public Optional<Evse> findByEvseIdAndStationId(Integer evseId, String stationId) {
        return evseRepository.findByEvseIdAndStationId(evseId, stationId);
    }

    @Override
    public List<Evse> findByStationId(String stationId) {
        return evseRepository.findByStationId(stationId);
    }

    @Override
    public Optional<Evse> findByEvseIdAndStationIdWithConnectors(Integer evseId, String stationId) {
        return evseRepository.findByEvseIdAndStationIdWithConnectors(evseId, stationId);
    }
}
