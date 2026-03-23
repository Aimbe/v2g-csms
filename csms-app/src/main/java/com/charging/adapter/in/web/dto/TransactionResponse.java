package com.charging.adapter.in.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
    Long id,
    String transactionId,
    Integer evseId,
    String stationId,
    Integer connectorId,
    String idToken,
    String eventType,
    String chargingState,
    LocalDateTime startTime,
    LocalDateTime stopTime,
    BigDecimal totalEnergy,
    String stopReason
) {}
