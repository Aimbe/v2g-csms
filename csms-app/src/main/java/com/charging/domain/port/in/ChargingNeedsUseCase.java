package com.charging.domain.port.in;

import com.charging.domain.entity.ChargingNeeds;
import com.charging.domain.enums.EnergyTransferModeEnum;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * EV 충전 요구사항(Charging Needs) 처리 유스케이스
 * OCPP 2.1 NotifyEVChargingNeeds 메시지 처리
 */
public interface ChargingNeedsUseCase {

    ChargingNeeds saveChargingNeeds(
            String stationId,
            Integer evseId,
            EnergyTransferModeEnum requestedEnergyTransfer,
            LocalDateTime departureTime,
            Integer targetSoc,
            Integer evEnergyCapacity,
            Integer evMaxCurrent,
            Integer evMaxVoltage,
            Integer evMaxPower
    );

    Optional<ChargingNeeds> getLatestChargingNeeds(String stationId, Integer evseId);
}
