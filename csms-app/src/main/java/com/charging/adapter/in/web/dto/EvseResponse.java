package com.charging.adapter.in.web.dto;

import java.math.BigDecimal;
import java.util.List;

public record EvseResponse(
    Long id,
    Integer evseId,
    String stationId,
    BigDecimal maxPower,
    String operationalStatus,
    List<ConnectorResponse> connectors
) {
    public static EvseResponse from(com.charging.domain.entity.Evse evse) {
        List<ConnectorResponse> connectorResponses = evse.getConnectors() != null
                ? evse.getConnectors().stream().map(ConnectorResponse::from).toList()
                : List.of();
        return new EvseResponse(
                evse.getId(),
                evse.getEvseId(),
                evse.getStationId(),
                evse.getMaxPower(),
                evse.getOperationalStatus().name(),
                connectorResponses
        );
    }
}
