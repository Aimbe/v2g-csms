package com.charging.adapter.out.persistence.repository;

import com.charging.domain.entity.Evse;
import com.charging.domain.enums.OperationalStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvseRepository extends JpaRepository<Evse, Long> {
    Optional<Evse> findByEvseIdAndStationId(Integer evseId, String stationId);
    List<Evse> findByStationId(String stationId);

    @Query("SELECT e FROM Evse e LEFT JOIN FETCH e.connectors WHERE e.evseId = :evseId AND e.stationId = :stationId")
    Optional<Evse> findByEvseIdAndStationIdWithConnectors(@Param("evseId") Integer evseId, @Param("stationId") String stationId);

    @Query("SELECT DISTINCT e FROM Evse e LEFT JOIN FETCH e.connectors WHERE e.stationId = :stationId")
    List<Evse> findByStationIdWithConnectors(@Param("stationId") String stationId);

    List<Evse> findByOperationalStatus(OperationalStatusEnum status);
    List<Evse> findByStationIdAndOperationalStatus(String stationId, OperationalStatusEnum status);
}
