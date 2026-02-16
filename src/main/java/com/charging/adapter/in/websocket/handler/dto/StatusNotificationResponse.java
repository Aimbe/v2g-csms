package com.charging.adapter.in.websocket.handler.dto;

public record StatusNotificationResponse() {
    public static StatusNotificationResponse empty() {
        return new StatusNotificationResponse();
    }
}
