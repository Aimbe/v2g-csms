package com.charging.adapter.out.persistence.repository;

import com.charging.domain.entity.ChargingNeeds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChargingNeedsRepository extends JpaRepository<ChargingNeeds, Long> {
    List<ChargingNeeds> findByStationIdAndEvseId(String stationId, Integer evseId);

    @Query("SELECT cn FROM ChargingNeeds cn WHERE cn.stationId = :stationId AND cn.evseId = :evseId ORDER BY cn.createdAt DESC LIMIT 1")
    Optional<ChargingNeeds> findLatestByStationIdAndEvseId(@Param("stationId") String stationId, @Param("evseId") Integer evseId);
}
