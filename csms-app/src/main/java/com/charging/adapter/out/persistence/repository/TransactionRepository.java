package com.charging.adapter.out.persistence.repository;

import com.charging.domain.entity.Transaction;
import com.charging.domain.enums.ChargingStateEnum;
import com.charging.domain.enums.TransactionEventEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByTransactionId(String transactionId);
    List<Transaction> findByStationId(String stationId);
    List<Transaction> findByEvseIdAndStationId(Integer evseId, String stationId);
    List<Transaction> findByIdToken(String idToken);
    List<Transaction> findByEventType(TransactionEventEnum eventType);
    List<Transaction> findByChargingState(ChargingStateEnum chargingState);

    @Query("SELECT t FROM Transaction t WHERE t.stopTime IS NULL AND t.stationId = :stationId AND t.evseId = :evseId")
    Optional<Transaction> findActiveTransactionByStationIdAndEvseId(@Param("stationId") String stationId,
                                                                    @Param("evseId") Integer evseId);

    @Query("SELECT t FROM Transaction t WHERE t.stopTime IS NULL AND t.stationId = :stationId")
    List<Transaction> findActiveTransactions(@Param("stationId") String stationId);

    @Query("SELECT t FROM Transaction t LEFT JOIN FETCH t.meterValues WHERE t.transactionId = :transactionId")
    Optional<Transaction> findByTransactionIdWithMeterValues(@Param("transactionId") String transactionId);

    @Query("SELECT t FROM Transaction t WHERE t.startTime BETWEEN :startDate AND :endDate")
    List<Transaction> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT t FROM Transaction t WHERE t.stationId = :stationId AND t.startTime BETWEEN :startDate AND :endDate")
    List<Transaction> findByStationIdAndDateRange(@Param("stationId") String stationId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.stopTime IS NULL")
    long countActiveTransactions();

    long countByStartTimeBetween(LocalDateTime start, LocalDateTime end);
}
