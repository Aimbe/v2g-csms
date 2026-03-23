package com.charging.domain.port.in;

import com.charging.domain.entity.Transaction;
import com.charging.domain.enums.ChargingStateEnum;
import java.util.List;

public interface TransactionUseCase {
    Transaction startTransaction(Integer evseId, String stationId, Integer connectorId, String idToken);
    Transaction stopTransaction(String transactionId, String stopReason);
    Transaction updateChargingState(String transactionId, ChargingStateEnum newState);
    List<Transaction> getActiveTransactions(String stationId);
}
