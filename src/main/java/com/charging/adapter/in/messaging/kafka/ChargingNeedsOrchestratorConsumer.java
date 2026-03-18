package com.charging.adapter.in.messaging.kafka;

import com.charging.application.outbox.dto.ChargingNeedsEventPayload;
import com.charging.application.service.V2gOrchestratorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.v2g.orchestrator.consumer", name = "enabled", havingValue = "true")
public class ChargingNeedsOrchestratorConsumer {

    private final ObjectMapper objectMapper;
    private final V2gOrchestratorService v2gOrchestratorService;

    @KafkaListener(
            topics = "${app.kafka.topics.v2g-needs:ocpp.v2g.charging-needs.v1}",
            groupId = "v2g-orchestrator"
    )
    public void consume(String payload) {
        try {
            ChargingNeedsEventPayload chargingNeedsEventPayload =
                    objectMapper.copy().findAndRegisterModules()
                            .readValue(payload, ChargingNeedsEventPayload.class);

            v2gOrchestratorService.orchestrate(chargingNeedsEventPayload);
        } catch (Exception e) {
            log.error("ChargingNeeds orchestration consumer 처리 실패: error={}", e.getMessage(), e);
            throw new IllegalStateException("ChargingNeeds orchestration consumer failed", e);
        }
    }
}
