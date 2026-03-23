package com.charging.adapter.out.persistence.repository;

import com.charging.domain.entity.ChargingProfile;
import com.charging.domain.enums.ChargingProfileKindEnum;
import com.charging.domain.enums.ChargingProfilePurposeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChargingProfileRepository extends JpaRepository<ChargingProfile, Long> {
    Optional<ChargingProfile> findByChargingProfileId(Integer chargingProfileId);
    List<ChargingProfile> findByStationId(String stationId);
    List<ChargingProfile> findByStationIdAndEvseId(String stationId, Integer evseId);
    List<ChargingProfile> findByTransactionId(String transactionId);
    List<ChargingProfile> findByChargingProfilePurpose(ChargingProfilePurposeEnum purpose);
    List<ChargingProfile> findByChargingProfileKind(ChargingProfileKindEnum kind);
    List<ChargingProfile> findByIsActiveTrue();
    List<ChargingProfile> findByStationIdAndIsActiveTrue(String stationId);

    @Query("SELECT cp FROM ChargingProfile cp WHERE cp.isActive = true AND (cp.validFrom IS NULL OR cp.validFrom <= :now) AND (cp.validTo IS NULL OR cp.validTo >= :now) AND cp.stationId = :stationId ORDER BY cp.stackLevel DESC")
    List<ChargingProfile> findActiveProfilesByStationId(@Param("stationId") String stationId, @Param("now") LocalDateTime now);

    @Query("SELECT cp FROM ChargingProfile cp WHERE cp.stationId = :stationId AND cp.isActive = true ORDER BY cp.stackLevel DESC")
    List<ChargingProfile> findByStationIdOrderByStackLevelDesc(@Param("stationId") String stationId);
}
