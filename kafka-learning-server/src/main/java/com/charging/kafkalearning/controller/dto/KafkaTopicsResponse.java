package com.charging.kafkalearning.controller.dto;

import java.util.List;

public record KafkaTopicsResponse(
        String defaultTopic,
        List<String> topics
) {
}
