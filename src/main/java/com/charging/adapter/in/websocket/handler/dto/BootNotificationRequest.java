package com.charging.adapter.in.websocket.handler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BootNotificationRequest(
    ChargingStationInfo chargingStation,
    String reason
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ChargingStationInfo(
        String model,
        String vendorName,
        String serialNumber,
        String firmwareVersion,
        Boolean v2gSupported,
        Boolean iso15118Supported
    ) {}
}
