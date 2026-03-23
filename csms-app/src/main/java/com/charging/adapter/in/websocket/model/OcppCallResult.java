package com.charging.adapter.in.websocket.model;

public record OcppCallResult(
    String messageId,
    String payload
) {
    public static final int MESSAGE_TYPE = 3;
}
