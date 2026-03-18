package com.charging.application.outbox;

import com.charging.application.outbox.dto.MeterValuesBatchPayload;
import com.charging.domain.entity.MeterValue;
import com.charging.domain.entity.OutboxEvent;
import com.charging.domain.entity.Transaction;
import com.charging.domain.enums.OutboxStatusEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class MeterValuesOutboxFactory {

    private final ObjectMapper objectMapper;
    private final String meteringTopic;

    public MeterValuesOutboxFactory(
            ObjectMapper objectMapper,
            @Value("${app.kafka.topics.metering:ocpp.meter-value.v1}") String meteringTopic
    ) {
        this.objectMapper = objectMapper.copy().findAndRegisterModules();
        this.meteringTopic = meteringTopic;
    }

    public OutboxEvent create(Transaction transaction, Integer evseId, List<MeterValue> meterValues) {
        MeterValuesBatchPayload payload = new MeterValuesBatchPayload(
                transaction.getStationId(),
                evseId,
                transaction.getTransactionId(),
                meterValues.size(),
                LocalDateTime.now(),
                meterValues.stream()
                        .map(meterValue -> new MeterValuesBatchPayload.MeterSamplePayload(
                                meterValue.getTimestamp(),
                                meterValue.getMeasurand(),
                                meterValue.getValue(),
                                meterValue.getUnit(),
                                meterValue.getLocation()
                        ))
                        .toList()
        );

        return OutboxEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .aggregateType("MeterValue")
                .aggregateId(transaction.getTransactionId())
                .topic(meteringTopic)
                .eventType("MeterValuesBatchRecorded")
                .eventVersion(1)
                .partitionKey(transaction.getStationId() + ":" + evseId)
                .payload(writePayload(payload))
                .status(OutboxStatusEnum.INIT)
                .retryCount(0)
                .occurredAt(LocalDateTime.now())
                .build();
    }

    private String writePayload(MeterValuesBatchPayload payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("MeterValues 이벤트 payload 직렬화에 실패했습니다.", e);
        }
    }
}
