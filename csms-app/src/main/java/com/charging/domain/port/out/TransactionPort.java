package com.charging.domain.port.out;

import com.charging.domain.entity.Transaction;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionPort {
    Transaction save(Transaction transaction);
    Optional<Transaction> findByTransactionId(String transactionId);
    Optional<Transaction> findActiveTransactionByStationIdAndEvseId(String stationId, Integer evseId);
    List<Transaction> findActiveTransactions(String stationId);
    List<Transaction> findByStationId(String stationId);
    Optional<Transaction> findByTransactionIdWithMeterValues(String transactionId);
    List<Transaction> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    long countActiveTransactions();
    List<Transaction> findAll();
    List<Transaction> findByStationIdAndDateRange(String stationId, LocalDateTime startDate, LocalDateTime endDate);
    long countByStartTimeBetween(LocalDateTime start, LocalDateTime end);
}
