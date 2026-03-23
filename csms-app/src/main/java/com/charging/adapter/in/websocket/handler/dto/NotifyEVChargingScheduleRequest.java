package com.charging.adapter.in.websocket.handler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NotifyEVChargingScheduleRequest(
    String timeBase,
    int evseId,
    ChargingScheduleInfo chargingSchedule
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ChargingScheduleInfo(
        int id,
        String chargingRateUnit,
        List<ChargingSchedulePeriodInfo> chargingSchedulePeriod
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ChargingSchedulePeriodInfo(
        int startPeriod,
        double limit,
        Integer numberPhases
    ) {}
}
