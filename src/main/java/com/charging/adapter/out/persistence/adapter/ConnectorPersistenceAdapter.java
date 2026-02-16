package com.charging.adapter.out.persistence.adapter;

import com.charging.adapter.out.persistence.repository.ConnectorRepository;
import com.charging.domain.entity.Connector;
import com.charging.domain.enums.ConnectorStatusEnum;
import com.charging.domain.port.out.ConnectorPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ConnectorPersistenceAdapter implements ConnectorPort {

    private final ConnectorRepository connectorRepository;

    @Override
    public Connector save(Connector connector) {
        return connectorRepository.save(connector);
    }

    @Override
    public Optional<Connector> findByEvseIdAndStationIdAndConnectorId(Integer evseId, String stationId, Integer connectorId) {
        return connectorRepository.findByEvseIdAndStationIdAndConnectorId(evseId, stationId, connectorId);
    }

    @Override
    public List<Connector> findByStationIdAndStatus(String stationId, ConnectorStatusEnum status) {
        return connectorRepository.findByStationIdAndStatus(stationId, status);
    }

    @Override
    public List<Connector> findAvailableConnectors(String stationId) {
        return connectorRepository.findAvailableConnectors(stationId);
    }
}
