package com.charging.adapter.in.websocket.handler.dto;

import java.time.Instant;

public record BootNotificationResponse(
    String currentTime,
    int interval,
    String status
) {
    public static BootNotificationResponse accepted(int intervalSeconds) {
        return new BootNotificationResponse(
            Instant.now().toString(),
            intervalSeconds,
            "Accepted"
        );
    }

    public static BootNotificationResponse rejected(String reason) {
        return new BootNotificationResponse(
            Instant.now().toString(),
            60,
            "Rejected"
        );
    }
}
