package com.charging.kafkalearning.service;

import com.charging.kafkalearning.config.KafkaLearningProperties;
import com.charging.kafkalearning.model.ConsumedKafkaMessage;
import com.charging.kafkalearning.model.PublishedKafkaMessage;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
public class KafkaLearningMessageService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaLearningMessageStore messageStore;
    private final KafkaLearningProperties properties;

    public KafkaLearningMessageService(
            KafkaTemplate<String, String> kafkaTemplate,
            KafkaLearningMessageStore messageStore,
            KafkaLearningProperties properties
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.messageStore = messageStore;
        this.properties = properties;
    }

    public List<String> topics() {
        return properties.topics();
    }

    public String defaultTopic() {
        return properties.defaultTopic();
    }

    public PublishedKafkaMessage publish(String topic, String key, String payload) {
        try {
            SendResult<String, String> sendResult = kafkaTemplate.send(resolveTopic(topic), key, payload)
                    .get(5, TimeUnit.SECONDS);

            return new PublishedKafkaMessage(
                    sendResult.getRecordMetadata().topic(),
                    sendResult.getRecordMetadata().partition(),
                    sendResult.getRecordMetadata().offset(),
                    key,
                    payload
            );
        } catch (Exception exception) {
            throw new IllegalStateException("Kafka 메시지 발행에 실패했습니다.", exception);
        }
    }

    public void recordConsumed(ConsumerRecord<String, String> record) {
        messageStore.append(new ConsumedKafkaMessage(
                record.topic(),
                record.partition(),
                record.offset(),
                record.key(),
                record.value(),
                Instant.now()
        ));
    }

    public List<ConsumedKafkaMessage> recentMessages(int limit) {
        return messageStore.recent(limit);
    }

    private String resolveTopic(String topic) {
        return (topic == null || topic.isBlank()) ? properties.defaultTopic() : topic;
    }
}
