package com.charging.application.outbox;

import com.charging.application.outbox.dto.TransactionEventPayload;
import com.charging.domain.entity.OutboxEvent;
import com.charging.domain.entity.Transaction;
import com.charging.domain.enums.OutboxStatusEnum;
import com.charging.domain.enums.TransactionEventEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class TransactionEventOutboxFactory {

    private final ObjectMapper objectMapper;
    private final String transactionTopic;

    public TransactionEventOutboxFactory(
            ObjectMapper objectMapper,
            @Value("${app.kafka.topics.transaction:ocpp.transaction.v1}") String transactionTopic
    ) {
        this.objectMapper = objectMapper.copy().findAndRegisterModules();
        this.transactionTopic = transactionTopic;
    }

    public OutboxEvent create(Transaction transaction) {
        TransactionEventPayload payload = new TransactionEventPayload(
                transaction.getTransactionId(),
                transaction.getStationId(),
                transaction.getEvseId(),
                transaction.getConnectorId(),
                transaction.getIdToken(),
                transaction.getEventType(),
                transaction.getChargingState(),
                transaction.getStartTime(),
                transaction.getStopTime(),
                transaction.getTotalEnergy(),
                transaction.getStopReason()
        );

        return OutboxEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .aggregateType("Transaction")
                .aggregateId(transaction.getTransactionId())
                .topic(transactionTopic)
                .eventType(resolveEventType(transaction.getEventType()))
                .eventVersion(1)
                .partitionKey(transaction.getTransactionId())
                .payload(writePayload(payload))
                .status(OutboxStatusEnum.INIT)
                .retryCount(0)
                .occurredAt(LocalDateTime.now())
                .build();
    }

    private String resolveEventType(TransactionEventEnum transactionEventEnum) {
        return switch (transactionEventEnum) {
            case STARTED -> "TransactionStarted";
            case UPDATED -> "TransactionStateUpdated";
            case ENDED -> "TransactionEnded";
        };
    }

    private String writePayload(TransactionEventPayload payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("트랜잭션 이벤트 payload 직렬화에 실패했습니다.", e);
        }
    }
}
