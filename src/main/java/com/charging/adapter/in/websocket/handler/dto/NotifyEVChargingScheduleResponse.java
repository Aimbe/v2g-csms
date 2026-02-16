package com.charging.adapter.in.websocket.handler.dto;

public record NotifyEVChargingScheduleResponse(
    String status
) {
    public static NotifyEVChargingScheduleResponse accepted() {
        return new NotifyEVChargingScheduleResponse("Accepted");
    }
}
