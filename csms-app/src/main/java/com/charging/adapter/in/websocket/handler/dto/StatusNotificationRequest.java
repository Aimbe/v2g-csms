package com.charging.adapter.in.websocket.handler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StatusNotificationRequest(
    String timestamp,
    String connectorStatus,
    int evseId,
    int connectorId
) {}
