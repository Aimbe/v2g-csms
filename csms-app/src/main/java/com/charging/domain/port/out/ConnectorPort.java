package com.charging.domain.port.out;

import com.charging.domain.entity.Connector;
import com.charging.domain.enums.ConnectorStatusEnum;
import java.util.List;
import java.util.Optional;

public interface ConnectorPort {
    Connector save(Connector connector);
    Optional<Connector> findByEvseIdAndStationIdAndConnectorId(Integer evseId, String stationId, Integer connectorId);
    List<Connector> findByStationIdAndStatus(String stationId, ConnectorStatusEnum status);
    List<Connector> findAvailableConnectors(String stationId);
    long countByStatus(ConnectorStatusEnum status);
}
