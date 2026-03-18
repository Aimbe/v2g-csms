package com.charging.adapter.out.messaging.kafka;

import com.charging.domain.entity.OutboxEvent;
import com.charging.domain.port.out.OutboxEventPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.outbox.relay", name = "enabled", havingValue = "true")
public class OutboxRelayPublisher {

    private final OutboxEventPort outboxEventPort;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @org.springframework.beans.factory.annotation.Value("${app.outbox.relay.batch-size:100}")
    private int batchSize;

    @org.springframework.beans.factory.annotation.Value("${app.outbox.relay.max-retry-count:5}")
    private int maxRetryCount;

    @Scheduled(fixedDelayString = "${app.outbox.relay.fixed-delay-ms:1000}")
    public void publishPendingEvents() {
        List<OutboxEvent> pendingEvents = outboxEventPort.findPendingEvents(batchSize, maxRetryCount);

        if (pendingEvents.isEmpty()) {
            return;
        }

        log.info("Outbox relay 실행: pendingEvents={}", pendingEvents.size());

        for (OutboxEvent outboxEvent : pendingEvents) {
            try {
                kafkaTemplate.send(
                        outboxEvent.getTopic(),
                        outboxEvent.getPartitionKey(),
                        outboxEvent.getPayload()
                ).get();

                outboxEvent.markPublished(LocalDateTime.now());
                outboxEventPort.save(outboxEvent);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                recordFailure(outboxEvent, e);
                break;
            } catch (Exception e) {
                recordFailure(outboxEvent, e);
            }
        }
    }

    private void recordFailure(OutboxEvent outboxEvent, Exception e) {
        log.error("Outbox publish 실패: eventId={}, topic={}, error={}",
                outboxEvent.getEventId(), outboxEvent.getTopic(), e.getMessage(), e);

        outboxEvent.recordFailure(trimMessage(e.getMessage()), maxRetryCount);
        outboxEventPort.save(outboxEvent);
    }

    private String trimMessage(String message) {
        if (message == null || message.isBlank()) {
            return "Unknown publish error";
        }

        return message.length() > 1000 ? message.substring(0, 1000) : message;
    }
}
