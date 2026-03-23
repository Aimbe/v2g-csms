package com.charging.adapter.in.websocket.handler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClearedChargingLimitRequest(
    String chargingLimitSource,
    Integer evseId
) {}
