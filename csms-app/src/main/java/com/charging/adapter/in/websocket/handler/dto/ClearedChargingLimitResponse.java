package com.charging.adapter.in.websocket.handler.dto;

public record ClearedChargingLimitResponse() {
    public static ClearedChargingLimitResponse empty() {
        return new ClearedChargingLimitResponse();
    }
}
