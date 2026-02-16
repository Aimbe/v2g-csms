package com.charging.adapter.in.websocket.handler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NotifyEVChargingNeedsRequest(
    int evseId,
    ChargingNeedsInfo chargingNeeds
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ChargingNeedsInfo(
        String requestedEnergyTransfer,
        String departureTime,
        ACChargingParameters acChargingParameters,
        DCChargingParameters dcChargingParameters
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ACChargingParameters(
        int evMaxCurrent,
        int evMaxVoltage,
        int evMinCurrent
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DCChargingParameters(
        int evMaxCurrent,
        int evMaxVoltage,
        int evMaxPower,
        Integer stateOfCharge,
        Integer evEnergyCapacity
    ) {}
}
