package com.charging.kafkalearning.controller.dto;

import java.time.Instant;

public record ConsumedKafkaMessageResponse(
        String topic,
        int partition,
        long offset,
        String key,
        String payload,
        Instant receivedAt
) {
}
