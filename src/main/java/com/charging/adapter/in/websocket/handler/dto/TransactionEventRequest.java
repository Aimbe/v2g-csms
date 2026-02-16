package com.charging.adapter.in.websocket.handler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TransactionEventRequest(
    String eventType,
    String timestamp,
    String triggerReason,
    int seqNo,
    TransactionInfo transactionInfo,
    IdTokenInfo idToken,
    EvseInfo evse
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TransactionInfo(
        String transactionId,
        String chargingState,
        String stoppedReason
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record IdTokenInfo(
        String idToken,
        String type
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record EvseInfo(
        int id,
        int connectorId
    ) {}
}
