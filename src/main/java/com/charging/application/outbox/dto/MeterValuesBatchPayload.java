package com.charging.application.outbox.dto;

import com.charging.domain.enums.MeasurandEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record MeterValuesBatchPayload(
        String stationId,
        Integer evseId,
        String transactionId,
        Integer meterCount,
        LocalDateTime recordedAt,
        List<MeterSamplePayload> samples
) {
    public record MeterSamplePayload(
            LocalDateTime timestamp,
            MeasurandEnum measurand,
            BigDecimal value,
            String unit,
            String location
    ) {
    }
}
