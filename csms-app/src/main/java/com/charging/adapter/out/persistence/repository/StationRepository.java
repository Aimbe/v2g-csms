package com.charging.adapter.out.persistence.repository;

import com.charging.domain.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {
    Optional<Station> findByStationId(String stationId);
    boolean existsByStationId(String stationId);

    @Query("SELECT s FROM Station s LEFT JOIN FETCH s.evses WHERE s.stationId = :stationId")
    Optional<Station> findByStationIdWithEvses(@Param("stationId") String stationId);
}
