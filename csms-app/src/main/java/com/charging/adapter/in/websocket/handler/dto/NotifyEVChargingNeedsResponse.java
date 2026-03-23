package com.charging.adapter.in.websocket.handler.dto;

public record NotifyEVChargingNeedsResponse(
    String status
) {
    public static NotifyEVChargingNeedsResponse accepted() {
        return new NotifyEVChargingNeedsResponse("Accepted");
    }

    public static NotifyEVChargingNeedsResponse rejected() {
        return new NotifyEVChargingNeedsResponse("Rejected");
    }
}
