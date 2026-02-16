package com.charging.application.service;

import com.charging.domain.entity.Station;
import com.charging.domain.port.in.BootNotificationUseCase;
import com.charging.domain.port.out.StationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BootNotificationServiceImpl implements BootNotificationUseCase {

    private final StationPort stationPort;

    @Override
    @Transactional
    public Optional<Station> processBootNotification(String stationId, String model, String vendorName, String reason) {
        log.info("BootNotification 처리: stationId={}, model={}, vendorName={}, reason={}",
                stationId, model, vendorName, reason);

        Optional<Station> station = stationPort.findByStationId(stationId);

        if (station.isPresent()) {
            log.info("등록된 충전소 확인: stationId={}", stationId);
        } else {
            log.warn("미등록 충전소 BootNotification: stationId={}", stationId);
        }

        return station;
    }
}
