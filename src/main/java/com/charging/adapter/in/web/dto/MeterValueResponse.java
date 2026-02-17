package com.charging.adapter.in.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MeterValueResponse(
    Long id,
    LocalDateTime timestamp,
    String measurand,
    BigDecimal value,
    String unit,
    String phase,
    String location
) {
    public static MeterValueResponse from(com.charging.domain.entity.MeterValue mv) {
        return new MeterValueResponse(
                mv.getId(),
                mv.getTimestamp(),
                mv.getMeasurand().name(),
                mv.getValue(),
                mv.getUnit(),
                mv.getPhase(),
                mv.getLocation()
        );
    }
}
