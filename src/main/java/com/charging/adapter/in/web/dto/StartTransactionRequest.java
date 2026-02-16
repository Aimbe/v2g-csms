package com.charging.adapter.in.web.dto;

public record StartTransactionRequest(
    Integer evseId,
    String stationId,
    Integer connectorId,
    String idToken
) {}
