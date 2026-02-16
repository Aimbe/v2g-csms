package com.charging.adapter.in.web;

import com.charging.domain.entity.Transaction;
import com.charging.domain.enums.ChargingStateEnum;
import com.charging.domain.port.in.TransactionUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionUseCase transactionUseCase;

    @PostMapping("/start")
    public ResponseEntity<Transaction> startTransaction(
            @RequestParam Integer evseId,
            @RequestParam String stationId,
            @RequestParam Integer connectorId,
            @RequestParam String idToken) {

        Transaction transaction = transactionUseCase.startTransaction(
                evseId, stationId, connectorId, idToken);

        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

    @PostMapping("/{transactionId}/stop")
    public ResponseEntity<Transaction> stopTransaction(
            @PathVariable String transactionId,
            @RequestParam(required = false, defaultValue = "Normal") String stopReason) {

        Transaction transaction = transactionUseCase.stopTransaction(transactionId, stopReason);

        return ResponseEntity.ok(transaction);
    }

    @PatchMapping("/{transactionId}/charging-state")
    public ResponseEntity<Transaction> updateChargingState(
            @PathVariable String transactionId,
            @RequestParam ChargingStateEnum chargingState) {

        Transaction transaction = transactionUseCase.updateChargingState(transactionId, chargingState);

        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/active")
    public ResponseEntity<List<Transaction>> getActiveTransactions(
            @RequestParam String stationId) {

        List<Transaction> transactions = transactionUseCase.getActiveTransactions(stationId);

        return ResponseEntity.ok(transactions);
    }
}
