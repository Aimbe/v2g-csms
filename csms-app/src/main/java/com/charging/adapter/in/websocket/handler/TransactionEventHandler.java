package com.charging.adapter.in.websocket.handler;

import com.charging.adapter.in.websocket.OcppAction;
import com.charging.adapter.in.websocket.OcppSession;
import com.charging.adapter.in.websocket.handler.dto.TransactionEventRequest;
import com.charging.adapter.in.websocket.handler.dto.TransactionEventResponse;
import com.charging.adapter.in.websocket.support.annotation.OcppPayload;
import com.charging.domain.enums.ChargingStateEnum;
import com.charging.domain.port.in.TransactionUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionEventHandler {

    private final TransactionUseCase transactionUseCase;

    @OcppAction("TransactionEvent")
    public TransactionEventResponse handle(@OcppPayload TransactionEventRequest request, OcppSession session) {
        log.info("TransactionEvent 수신: stationId={}, eventType={}, transactionId={}",
                session.getStationId(),
                request.eventType(),
                request.transactionInfo() != null ? request.transactionInfo().transactionId() : "N/A");

        String eventType = request.eventType();

        switch (eventType) {
            case "Started" -> {
                if (request.evse() != null && request.idToken() != null) {
                    transactionUseCase.startTransaction(
                            request.evse().id(),
                            session.getStationId(),
                            request.evse().connectorId(),
                            request.idToken().idToken()
                    );
                }
            }
            case "Updated" -> {
                if (request.transactionInfo() != null && request.transactionInfo().chargingState() != null) {
                    try {
                        ChargingStateEnum state = ChargingStateEnum.valueOf(
                                request.transactionInfo().chargingState().toUpperCase());
                        transactionUseCase.updateChargingState(
                                request.transactionInfo().transactionId(),
                                state
                        );
                    } catch (IllegalArgumentException e) {
                        log.warn("알 수 없는 충전 상태: {}", request.transactionInfo().chargingState());
                    }
                }
            }
            case "Ended" -> {
                if (request.transactionInfo() != null) {
                    String stopReason = request.transactionInfo().stoppedReason() != null
                            ? request.transactionInfo().stoppedReason()
                            : "Normal";
                    transactionUseCase.stopTransaction(
                            request.transactionInfo().transactionId(),
                            stopReason
                    );
                }
            }
            default -> log.warn("알 수 없는 이벤트 유형: {}", eventType);
        }

        return TransactionEventResponse.accepted();
    }
}
