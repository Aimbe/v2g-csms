package com.charging.adapter.in.websocket.model;

public record OcppCall(
    String messageId,
    String action,
    String payload
) {
    public static final int MESSAGE_TYPE = 2;
}
