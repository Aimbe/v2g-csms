package com.charging.adapter.in.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ChargingNeedsResponse(
    Long id,
    String stationId,
    Integer evseId,
    String requestedEnergyTransfer,
    LocalDateTime departureTime,
    BigDecimal acMaxCurrent,
    BigDecimal acMaxVoltage,
    BigDecimal acMinCurrent,
    BigDecimal dcMaxCurrent,
    BigDecimal dcMaxVoltage,
    BigDecimal dcMaxPower,
    BigDecimal dcMinCurrent,
    BigDecimal dcMinVoltage,
    BigDecimal dcMinPower,
    Integer dcTargetSoc,
    Integer dcBulkSoc,
    Integer dcFullSoc,
    BigDecimal dcEnergyCapacity
) {
    public static ChargingNeedsResponse from(com.charging.domain.entity.ChargingNeeds cn) {
        return new ChargingNeedsResponse(
                cn.getId(),
                cn.getStationId(),
                cn.getEvseId(),
                cn.getRequestedEnergyTransfer().name(),
                cn.getDepartureTime(),
                cn.getAcMaxCurrent(),
                cn.getAcMaxVoltage(),
                cn.getAcMinCurrent(),
                cn.getDcMaxCurrent(),
                cn.getDcMaxVoltage(),
                cn.getDcMaxPower(),
                cn.getDcMinCurrent(),
                cn.getDcMinVoltage(),
                cn.getDcMinPower(),
                cn.getDcTargetSoc(),
                cn.getDcBulkSoc(),
                cn.getDcFullSoc(),
                cn.getDcEnergyCapacity()
        );
    }
}
