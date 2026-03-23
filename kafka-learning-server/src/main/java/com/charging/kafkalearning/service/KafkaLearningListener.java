package com.charging.kafkalearning.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaLearningListener {

    private final KafkaLearningMessageService messageService;

    public KafkaLearningListener(KafkaLearningMessageService messageService) {
        this.messageService = messageService;
    }

    @KafkaListener(
            id = "kafka-learning-server",
            topics = "#{'${learning.kafka.topics}'.split(',')}",
            groupId = "${learning.kafka.consumer-group}"
    )
    public void consume(ConsumerRecord<String, String> record) {
        messageService.recordConsumed(record);
    }
}
