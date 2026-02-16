package com.charging.adapter.out.persistence.adapter;

import com.charging.adapter.out.persistence.repository.ChargingNeedsRepository;
import com.charging.domain.entity.ChargingNeeds;
import com.charging.domain.port.out.ChargingNeedsPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ChargingNeedsPersistenceAdapter implements ChargingNeedsPort {

    private final ChargingNeedsRepository chargingNeedsRepository;

    @Override
    public ChargingNeeds save(ChargingNeeds chargingNeeds) {
        return chargingNeedsRepository.save(chargingNeeds);
    }

    @Override
    public Optional<ChargingNeeds> findById(Long id) {
        return chargingNeedsRepository.findById(id);
    }

    @Override
    public List<ChargingNeeds> findByStationIdAndEvseId(String stationId, Integer evseId) {
        return chargingNeedsRepository.findByStationIdAndEvseId(stationId, evseId);
    }

    @Override
    public Optional<ChargingNeeds> findLatestByStationIdAndEvseId(String stationId, Integer evseId) {
        return chargingNeedsRepository.findLatestByStationIdAndEvseId(stationId, evseId);
    }
}
