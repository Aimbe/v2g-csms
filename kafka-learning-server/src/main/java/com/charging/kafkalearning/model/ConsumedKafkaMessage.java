package com.charging.kafkalearning.model;

import java.time.Instant;

public record ConsumedKafkaMessage(
        String topic,
        int partition,
        long offset,
        String key,
        String payload,
        Instant receivedAt
) {
}
