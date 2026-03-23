package com.charging.kafkalearning.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public KafkaAdmin.NewTopics learningKafkaTopics(KafkaLearningProperties properties) {
        NewTopic[] topics = properties.topics().stream()
                .map(topic -> TopicBuilder.name(topic)
                        .partitions(1)
                        .replicas(1)
                        .build())
                .toArray(NewTopic[]::new);

        return new KafkaAdmin.NewTopics(topics);
    }
}
