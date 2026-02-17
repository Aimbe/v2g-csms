package com.charging.adapter.in.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ChargingProfileResponse(
    Long id,
    Integer chargingProfileId,
    String stationId,
    Integer evseId,
    String transactionId,
    Integer stackLevel,
    String chargingProfilePurpose,
    String chargingProfileKind,
    LocalDateTime validFrom,
    LocalDateTime validTo,
    Integer duration,
    LocalDateTime startSchedule,
    String chargingRateUnit,
    BigDecimal minChargingRate,
    Boolean isActive,
    BigDecimal maxDischargePower,
    BigDecimal minDischargePower,
    String dischargeRateUnit
) {
    public static ChargingProfileResponse from(com.charging.domain.entity.ChargingProfile cp) {
        return new ChargingProfileResponse(
                cp.getId(),
                cp.getChargingProfileId(),
                cp.getStationId(),
                cp.getEvseId(),
                cp.getTransactionId(),
                cp.getStackLevel(),
                cp.getChargingProfilePurpose().name(),
                cp.getChargingProfileKind().name(),
                cp.getValidFrom(),
                cp.getValidTo(),
                cp.getDuration(),
                cp.getStartSchedule(),
                cp.getChargingRateUnit(),
                cp.getMinChargingRate(),
                cp.getIsActive(),
                cp.getMaxDischargePower(),
                cp.getMinDischargePower(),
                cp.getDischargeRateUnit()
        );
    }
}
