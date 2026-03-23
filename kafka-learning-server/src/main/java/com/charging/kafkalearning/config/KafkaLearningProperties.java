package com.charging.kafkalearning.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "learning.kafka")
public record KafkaLearningProperties(
        @NotEmpty List<@NotBlank String> topics,
        @NotBlank String consumerGroup,
        @Min(1) int historySize
) {

    public String defaultTopic() {
        return topics.getFirst();
    }
}
