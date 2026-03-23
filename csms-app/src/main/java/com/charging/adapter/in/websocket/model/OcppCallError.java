package com.charging.adapter.in.websocket.model;

public record OcppCallError(
    String messageId,
    OcppErrorCode errorCode,
    String errorDescription,
    String errorDetails
) {
    public static final int MESSAGE_TYPE = 4;
}
