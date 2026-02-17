package com.charging.adapter.out.persistence.repository;

import com.charging.domain.entity.Connector;
import com.charging.domain.enums.ConnectorStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConnectorRepository extends JpaRepository<Connector, Long> {
    Optional<Connector> findByEvseIdAndStationIdAndConnectorId(Integer evseId, String stationId, Integer connectorId);
    List<Connector> findByEvseIdAndStationId(Integer evseId, String stationId);
    List<Connector> findByStationId(String stationId);
    List<Connector> findByStatus(ConnectorStatusEnum status);
    List<Connector> findByStationIdAndStatus(String stationId, ConnectorStatusEnum status);

    @Query("SELECT c FROM Connector c WHERE c.maxPower >= :minPower AND c.stationId = :stationId")
    List<Connector> findByMinPowerGreaterThanEqualAndStationId(@Param("minPower") java.math.BigDecimal minPower, @Param("stationId") String stationId);

    @Query("SELECT c FROM Connector c WHERE c.status = 'AVAILABLE' AND c.stationId = :stationId")
    List<Connector> findAvailableConnectors(@Param("stationId") String stationId);

    long countByStatus(ConnectorStatusEnum status);
}
