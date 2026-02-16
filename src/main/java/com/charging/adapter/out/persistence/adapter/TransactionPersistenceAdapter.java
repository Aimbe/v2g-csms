package com.charging.adapter.out.persistence.adapter;

import com.charging.adapter.out.persistence.repository.TransactionRepository;
import com.charging.domain.entity.Transaction;
import com.charging.domain.port.out.TransactionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TransactionPersistenceAdapter implements TransactionPort {

    private final TransactionRepository transactionRepository;

    @Override
    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public Optional<Transaction> findByTransactionId(String transactionId) {
        return transactionRepository.findByTransactionId(transactionId);
    }

    @Override
    public List<Transaction> findActiveTransactions(String stationId) {
        return transactionRepository.findActiveTransactions(stationId);
    }

    @Override
    public List<Transaction> findByStationId(String stationId) {
        return transactionRepository.findByStationId(stationId);
    }

    @Override
    public Optional<Transaction> findByTransactionIdWithMeterValues(String transactionId) {
        return transactionRepository.findByTransactionIdWithMeterValues(transactionId);
    }

    @Override
    public List<Transaction> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByDateRange(startDate, endDate);
    }
}
