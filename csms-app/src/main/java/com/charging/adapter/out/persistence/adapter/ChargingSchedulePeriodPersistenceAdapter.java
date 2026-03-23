package com.charging.adapter.out.persistence.adapter;

import com.charging.adapter.out.persistence.repository.ChargingSchedulePeriodRepository;
import com.charging.domain.entity.ChargingSchedulePeriod;
import com.charging.domain.port.out.ChargingSchedulePeriodPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChargingSchedulePeriodPersistenceAdapter implements ChargingSchedulePeriodPort {

    private final ChargingSchedulePeriodRepository chargingSchedulePeriodRepository;

    @Override
    public ChargingSchedulePeriod save(ChargingSchedulePeriod period) {
        return chargingSchedulePeriodRepository.save(period);
    }

    @Override
    public List<ChargingSchedulePeriod> saveAll(List<ChargingSchedulePeriod> periods) {
        return chargingSchedulePeriodRepository.saveAll(periods);
    }

    @Override
    public List<ChargingSchedulePeriod> findByStationIdAndEvseId(String stationId, Integer evseId) {
        return chargingSchedulePeriodRepository.findByStationIdAndEvseId(stationId, evseId);
    }

    @Override
    @Transactional
    public void deleteByStationIdAndEvseId(String stationId, Integer evseId) {
        chargingSchedulePeriodRepository.deleteByStationIdAndEvseId(stationId, evseId);
    }
}
