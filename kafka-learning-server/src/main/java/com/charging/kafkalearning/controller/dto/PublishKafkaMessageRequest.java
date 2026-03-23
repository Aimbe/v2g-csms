package com.charging.kafkalearning.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record PublishKafkaMessageRequest(
        String topic,
        String key,
        @NotBlank String payload
) {
}
