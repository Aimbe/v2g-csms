package com.charging.adapter.in.websocket.handler.dto;

import java.time.Instant;

public record HeartbeatResponse(
    String currentTime
) {
    public static HeartbeatResponse now() {
        return new HeartbeatResponse(Instant.now().toString());
    }
}
