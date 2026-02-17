package com.charging.adapter.in.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ChargingProfileRequest(
    String stationId,
    Integer evseId,
    Integer stackLevel,
    String chargingProfilePurpose,
    String chargingProfileKind,
    String chargingRateUnit,
    LocalDateTime validFrom,
    LocalDateTime validTo,
    BigDecimal minChargingRate,
    BigDecimal maxDischargePower,
    BigDecimal minDischargePower,
    String dischargeRateUnit
) {}
