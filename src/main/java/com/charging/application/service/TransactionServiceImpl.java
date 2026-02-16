package com.charging.application.service;

import com.charging.domain.entity.Evse;
import com.charging.domain.entity.Transaction;
import com.charging.domain.enums.ChargingStateEnum;
import com.charging.domain.enums.TransactionEventEnum;
import com.charging.domain.exception.ResourceNotFoundException;
import com.charging.domain.port.in.TransactionUseCase;
import com.charging.domain.port.out.EvsePort;
import com.charging.domain.port.out.TransactionPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionServiceImpl implements TransactionUseCase {

    private final TransactionPort transactionPort;
    private final EvsePort evsePort;

    @Override
    @Transactional
    public Transaction startTransaction(Integer evseId, String stationId, Integer connectorId, String idToken) {
        log.info("트랜잭션 시작 요청: evseId={}, stationId={}, connectorId={}, idToken={}",
                evseId, stationId, connectorId, idToken);

        Evse evse = evsePort.findByEvseIdAndStationId(evseId, stationId)
                .orElseThrow(() -> new ResourceNotFoundException("EVSE", "evseId-stationId",
                        evseId + "-" + stationId));

        String transactionId = generateTransactionId();

        Transaction transaction = Transaction.builder()
                .transactionId(transactionId)
                .evseId(evseId)
                .stationId(stationId)
                .connectorId(connectorId)
                .idToken(idToken)
                .eventType(TransactionEventEnum.STARTED)
                .chargingState(ChargingStateEnum.IDLE)
                .startTime(LocalDateTime.now())
                .build();

        evse.addTransaction(transaction);

        Transaction savedTransaction = transactionPort.save(transaction);
        log.info("트랜잭션 시작 완료: transactionId={}", transactionId);

        return savedTransaction;
    }

    @Override
    @Transactional
    public Transaction stopTransaction(String transactionId, String stopReason) {
        log.info("트랜잭션 종료 요청: transactionId={}, stopReason={}", transactionId, stopReason);

        Transaction transaction = transactionPort.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "transactionId", transactionId));

        transaction.stop(LocalDateTime.now(), stopReason);
        transaction.calculateTotalEnergy();

        Transaction savedTransaction = transactionPort.save(transaction);
        log.info("트랜잭션 종료 완료: transactionId={}, totalEnergy={} kWh",
                transactionId, transaction.getTotalEnergy());

        return savedTransaction;
    }

    @Override
    @Transactional
    public Transaction updateChargingState(String transactionId, ChargingStateEnum newState) {
        log.info("충전 상태 업데이트: transactionId={}, newState={}", transactionId, newState);

        Transaction transaction = transactionPort.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "transactionId", transactionId));

        transaction.updateChargingState(newState);

        return transactionPort.save(transaction);
    }

    @Override
    public List<Transaction> getActiveTransactions(String stationId) {
        return transactionPort.findActiveTransactions(stationId);
    }

    private String generateTransactionId() {
        return "TXN-" + System.currentTimeMillis();
    }
}
