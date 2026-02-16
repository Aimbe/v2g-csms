package com.charging.adapter.out.persistence.repository;

import com.charging.domain.entity.MeterValue;
import com.charging.domain.enums.MeasurandEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MeterValueRepository extends JpaRepository<MeterValue, Long> {
    List<MeterValue> findByTransactionIdFkOrderByTimestampAsc(Long transactionId);
    List<MeterValue> findByMeasurand(MeasurandEnum measurand);
    List<MeterValue> findByTransactionIdFkAndMeasurand(Long transactionId, MeasurandEnum measurand);

    @Query("SELECT m FROM MeterValue m WHERE m.timestamp BETWEEN :startDate AND :endDate ORDER BY m.timestamp ASC")
    List<MeterValue> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT m FROM MeterValue m WHERE m.transactionIdFk = :transactionId ORDER BY m.timestamp DESC LIMIT 1")
    MeterValue findLatestByTransactionId(@Param("transactionId") Long transactionId);

    @Query("SELECT m FROM MeterValue m WHERE m.measurand = 'ENERGY_ACTIVE_IMPORT_REGISTER' AND m.transactionIdFk = :transactionId ORDER BY m.timestamp ASC")
    List<MeterValue> findEnergyValuesByTransactionId(@Param("transactionId") Long transactionId);
}
