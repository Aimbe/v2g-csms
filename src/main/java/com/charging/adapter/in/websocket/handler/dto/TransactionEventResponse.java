package com.charging.adapter.in.websocket.handler.dto;

public record TransactionEventResponse(
    int totalCost,
    int chargingPriority,
    IdTokenInfoResult idTokenInfo
) {
    public record IdTokenInfoResult(
        String status
    ) {}

    public static TransactionEventResponse accepted() {
        return new TransactionEventResponse(0, 0, new IdTokenInfoResult("Accepted"));
    }
}
