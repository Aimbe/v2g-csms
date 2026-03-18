package com.charging.application.outbox.dto;

import com.charging.domain.enums.EnergyTransferModeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ChargingNeedsEventPayload(
        Long chargingNeedsId,
        String stationId,
        Integer evseId,
        EnergyTransferModeEnum requestedEnergyTransfer,
        LocalDateTime departureTime,
        BigDecimal dcMaxPower,
        Integer dcTargetSoc,
        BigDecimal dcEnergyCapacity
) {
}
