package com.charging.adapter.in.web;

import com.charging.adapter.in.web.dto.MeterValueResponse;
import com.charging.adapter.in.web.dto.TransactionResponse;
import com.charging.domain.entity.Transaction;
import com.charging.domain.enums.ChargingStateEnum;
import com.charging.domain.port.in.TransactionUseCase;
import com.charging.domain.port.out.TransactionPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionUseCase transactionUseCase;
    private final TransactionPort transactionPort;

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

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAllTransactions(
            @RequestParam(required = false) String stationId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<Transaction> transactions;
        if (stationId != null && startDate != null && endDate != null) {
            transactions = transactionPort.findByStationIdAndDateRange(stationId, startDate, endDate);
        } else if (startDate != null && endDate != null) {
            transactions = transactionPort.findByDateRange(startDate, endDate);
        } else if (stationId != null) {
            transactions = transactionPort.findByStationId(stationId);
        } else {
            transactions = transactionPort.findAll();
        }

        List<TransactionResponse> responses = transactions.stream()
                .map(t -> new TransactionResponse(
                        t.getId(), t.getTransactionId(), t.getEvseId(), t.getStationId(),
                        t.getConnectorId(), t.getIdToken(),
                        t.getEventType() != null ? t.getEventType().name() : null,
                        t.getChargingState() != null ? t.getChargingState().name() : null,
                        t.getStartTime(), t.getStopTime(), t.getTotalEnergy(), t.getStopReason()))
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{transactionId}/meter-values")
    public ResponseEntity<List<MeterValueResponse>> getTransactionMeterValues(
            @PathVariable String transactionId) {
        var txnOpt = transactionPort.findByTransactionIdWithMeterValues(transactionId);
        if (txnOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<MeterValueResponse> responses = txnOpt.get().getMeterValues().stream()
                .map(MeterValueResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
