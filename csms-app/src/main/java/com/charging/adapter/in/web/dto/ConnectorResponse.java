package com.charging.adapter.in.web.dto;

import java.math.BigDecimal;

public record ConnectorResponse(
    Long id,
    Integer connectorId,
    Integer evseId,
    String stationId,
    BigDecimal maxPower,
    BigDecimal minPower,
    String status
) {
    public static ConnectorResponse from(com.charging.domain.entity.Connector connector) {
        return new ConnectorResponse(
                connector.getId(),
                connector.getConnectorId(),
                connector.getEvseId(),
                connector.getStationId(),
                connector.getMaxPower(),
                connector.getMinPower(),
                connector.getStatus().name()
        );
    }
}
