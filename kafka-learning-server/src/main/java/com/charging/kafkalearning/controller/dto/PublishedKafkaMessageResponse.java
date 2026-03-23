package com.charging.kafkalearning.controller.dto;

public record PublishedKafkaMessageResponse(
        String topic,
        int partition,
        long offset,
        String key,
        String payload
) {
}
