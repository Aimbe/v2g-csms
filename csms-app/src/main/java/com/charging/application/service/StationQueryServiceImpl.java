package com.charging.application.service;

import com.charging.domain.entity.Station;
import com.charging.domain.exception.ResourceNotFoundException;
import com.charging.domain.port.in.StationQueryUseCase;
import com.charging.domain.port.out.StationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StationQueryServiceImpl implements StationQueryUseCase {

    private final StationPort stationPort;

    @Override
    public List<Station> getAllStations() {
        return stationPort.findAll();
    }

    @Override
    public Station getStationDetail(String stationId) {
        return stationPort.findByStationIdWithEvses(stationId)
                .orElseThrow(() -> new ResourceNotFoundException("Station", "stationId", stationId));
    }
}
