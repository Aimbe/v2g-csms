package com.charging.kafkalearning.model;

public record PublishedKafkaMessage(
        String topic,
        int partition,
        long offset,
        String key,
        String payload
) {
}
