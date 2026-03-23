package com.charging.application.outbox;

import com.charging.application.outbox.dto.ChargingNeedsEventPayload;
import com.charging.domain.entity.ChargingNeeds;
import com.charging.domain.entity.OutboxEvent;
import com.charging.domain.enums.OutboxStatusEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class ChargingNeedsOutboxFactory {

    private final ObjectMapper objectMapper;
    private final String chargingNeedsTopic;

    public ChargingNeedsOutboxFactory(
            ObjectMapper objectMapper,
            @Value("${app.kafka.topics.v2g-needs:ocpp.v2g.charging-needs.v1}") String chargingNeedsTopic
    ) {
        this.objectMapper = objectMapper.copy().findAndRegisterModules();
        this.chargingNeedsTopic = chargingNeedsTopic;
    }

    public OutboxEvent create(ChargingNeeds chargingNeeds) {
        ChargingNeedsEventPayload payload = new ChargingNeedsEventPayload(
                chargingNeeds.getId(),
                chargingNeeds.getStationId(),
                chargingNeeds.getEvseId(),
                chargingNeeds.getRequestedEnergyTransfer(),
                chargingNeeds.getDepartureTime(),
                chargingNeeds.getDcMaxPower(),
                chargingNeeds.getDcTargetSoc(),
                chargingNeeds.getDcEnergyCapacity()
        );

        return OutboxEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .aggregateType("ChargingNeeds")
                .aggregateId(String.valueOf(chargingNeeds.getId()))
                .topic(chargingNeedsTopic)
                .eventType("ChargingNeedsReceived")
                .eventVersion(1)
                .partitionKey(chargingNeeds.getStationId() + ":" + chargingNeeds.getEvseId())
                .payload(writePayload(payload))
                .status(OutboxStatusEnum.INIT)
                .retryCount(0)
                .occurredAt(LocalDateTime.now())
                .build();
    }

    private String writePayload(ChargingNeedsEventPayload payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("ChargingNeeds 이벤트 payload 직렬화에 실패했습니다.", e);
        }
    }
}
