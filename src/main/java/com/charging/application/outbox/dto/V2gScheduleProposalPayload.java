package com.charging.application.outbox.dto;

import com.charging.domain.enums.EnergyTransferModeEnum;
import com.charging.domain.enums.V2gProposalActionEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record V2gScheduleProposalPayload(
        Long proposalId,
        String stationId,
        Integer evseId,
        Long sourceChargingNeedsId,
        EnergyTransferModeEnum requestedEnergyTransfer,
        V2gProposalActionEnum proposalAction,
        String chargingRateUnit,
        BigDecimal proposedPower,
        Integer targetSoc,
        LocalDateTime validUntil,
        String reason
) {
}
