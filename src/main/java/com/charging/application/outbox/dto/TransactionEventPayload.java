package com.charging.application.outbox.dto;

import com.charging.domain.enums.ChargingStateEnum;
import com.charging.domain.enums.TransactionEventEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionEventPayload(
        String transactionId,
        String stationId,
        Integer evseId,
        Integer connectorId,
        String idToken,
        TransactionEventEnum eventType,
        ChargingStateEnum chargingState,
        LocalDateTime startTime,
        LocalDateTime stopTime,
        BigDecimal totalEnergy,
        String stopReason
) {
}
