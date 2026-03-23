package com.charging.adapter.in.messaging.kafka;

import com.charging.application.service.DashboardProjectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.dashboard.projection.consumer", name = "enabled", havingValue = "true")
public class DashboardTransactionProjectionConsumer {

    private final DashboardProjectionService dashboardProjectionService;

    @KafkaListener(
            topics = "${app.kafka.topics.transaction:ocpp.transaction.v1}",
            groupId = "dashboard-projection"
    )
    public void consume(String payload) {
        log.debug("Dashboard projection consumer 수신: payloadSize={}", payload != null ? payload.length() : 0);
        dashboardProjectionService.refreshProjection();
    }
}
