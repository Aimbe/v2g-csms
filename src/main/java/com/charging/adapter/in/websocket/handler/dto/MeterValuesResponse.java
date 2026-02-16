package com.charging.adapter.in.websocket.handler.dto;

public record MeterValuesResponse() {
    public static MeterValuesResponse empty() {
        return new MeterValuesResponse();
    }
}
