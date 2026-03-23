package com.charging.application.outbox;

import com.charging.application.outbox.dto.V2gScheduleProposalPayload;
import com.charging.domain.entity.OutboxEvent;
import com.charging.domain.entity.V2gScheduleProposal;
import com.charging.domain.enums.OutboxStatusEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class V2gScheduleProposalOutboxFactory {

    private final ObjectMapper objectMapper;
    private final String scheduleTopic;

    public V2gScheduleProposalOutboxFactory(
            ObjectMapper objectMapper,
            @Value("${app.kafka.topics.v2g-schedule:ocpp.v2g.charging-schedule.v1}") String scheduleTopic
    ) {
        this.objectMapper = objectMapper.copy().findAndRegisterModules();
        this.scheduleTopic = scheduleTopic;
    }

    public OutboxEvent create(V2gScheduleProposal proposal) {
        V2gScheduleProposalPayload payload = new V2gScheduleProposalPayload(
                proposal.getId(),
                proposal.getStationId(),
                proposal.getEvseId(),
                proposal.getSourceChargingNeedsId(),
                proposal.getRequestedEnergyTransfer(),
                proposal.getProposalAction(),
                proposal.getChargingRateUnit(),
                proposal.getProposedPower(),
                proposal.getTargetSoc(),
                proposal.getValidUntil(),
                proposal.getReason()
        );

        return OutboxEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .aggregateType("V2gScheduleProposal")
                .aggregateId(String.valueOf(proposal.getId()))
                .topic(scheduleTopic)
                .eventType("V2gScheduleProposed")
                .eventVersion(1)
                .partitionKey(proposal.getStationId() + ":" + proposal.getEvseId())
                .payload(writePayload(payload))
                .status(OutboxStatusEnum.INIT)
                .retryCount(0)
                .occurredAt(LocalDateTime.now())
                .build();
    }

    private String writePayload(V2gScheduleProposalPayload payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("V2G schedule proposal payload 직렬화에 실패했습니다.", e);
        }
    }
}
