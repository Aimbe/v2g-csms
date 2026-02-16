package com.charging.application.service;

import com.charging.domain.entity.ChargingNeeds;
import com.charging.domain.enums.EnergyTransferModeEnum;
import com.charging.domain.port.in.ChargingNeedsUseCase;
import com.charging.domain.port.out.ChargingNeedsPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChargingNeedsServiceImpl implements ChargingNeedsUseCase {

    private final ChargingNeedsPort chargingNeedsPort;

    @Override
    @Transactional
    public ChargingNeeds saveChargingNeeds(String stationId, Integer evseId,
                                            EnergyTransferModeEnum requestedEnergyTransfer,
                                            LocalDateTime departureTime,
                                            Integer targetSoc, Integer evEnergyCapacity,
                                            Integer evMaxCurrent, Integer evMaxVoltage, Integer evMaxPower) {
        log.info("EV 충전 요구사항 저장: stationId={}, evseId={}, mode={}",
                stationId, evseId, requestedEnergyTransfer);

        ChargingNeeds chargingNeeds = ChargingNeeds.builder()
                .stationId(stationId)
                .evseId(evseId)
                .requestedEnergyTransfer(requestedEnergyTransfer)
                .departureTime(departureTime)
                .dcTargetSoc(targetSoc)
                .dcEnergyCapacity(evEnergyCapacity != null ? new BigDecimal(evEnergyCapacity) : null)
                .dcMaxCurrent(evMaxCurrent != null ? new BigDecimal(evMaxCurrent) : null)
                .dcMaxVoltage(evMaxVoltage != null ? new BigDecimal(evMaxVoltage) : null)
                .dcMaxPower(evMaxPower != null ? new BigDecimal(evMaxPower) : null)
                .build();

        ChargingNeeds saved = chargingNeedsPort.save(chargingNeeds);
        log.info("EV 충전 요구사항 저장 완료: id={}", saved.getId());

        return saved;
    }

    @Override
    public Optional<ChargingNeeds> getLatestChargingNeeds(String stationId, Integer evseId) {
        log.info("최신 충전 요구사항 조회: stationId={}, evseId={}", stationId, evseId);
        return chargingNeedsPort.findLatestByStationIdAndEvseId(stationId, evseId);
    }
}
