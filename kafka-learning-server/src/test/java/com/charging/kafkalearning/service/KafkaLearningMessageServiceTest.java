package com.charging.kafkalearning.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.charging.kafkalearning.config.KafkaLearningProperties;
import com.charging.kafkalearning.model.PublishedKafkaMessage;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

@ExtendWith(MockitoExtension.class)
class KafkaLearningMessageServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    private KafkaLearningMessageService messageService;

    @BeforeEach
    void setUp() {
        KafkaLearningProperties properties = new KafkaLearningProperties(
                List.of("learning.demo.v1", "learning.demo.retry.v1"),
                "kafka-learning-server",
                5
        );
        KafkaLearningMessageStore store = new KafkaLearningMessageStore(properties);
        messageService = new KafkaLearningMessageService(kafkaTemplate, store, properties);
    }

    @Test
    void publishUsesDefaultTopicWhenRequestTopicIsBlank() {
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>("learning.demo.v1", "station-1", "hello kafka");
        RecordMetadata metadata = new RecordMetadata(
                new TopicPartition("learning.demo.v1", 0),
                0,
                15,
                System.currentTimeMillis(),
                0L,
                8,
                11
        );
        SendResult<String, String> sendResult = new SendResult<>(producerRecord, metadata);
        when(kafkaTemplate.send("learning.demo.v1", "station-1", "hello kafka"))
                .thenReturn(CompletableFuture.completedFuture(sendResult));

        PublishedKafkaMessage published = messageService.publish(" ", "station-1", "hello kafka");

        assertThat(published.topic()).isEqualTo("learning.demo.v1");
        assertThat(published.partition()).isZero();
        assertThat(published.offset()).isEqualTo(15);
        assertThat(published.key()).isEqualTo("station-1");
        assertThat(published.payload()).isEqualTo("hello kafka");
    }

    @Test
    void recordConsumedStoresNewestMessagesFirst() {
        messageService.recordConsumed(new ConsumerRecord<>("learning.demo.v1", 0, 10L, "station-1", "first"));
        messageService.recordConsumed(new ConsumerRecord<>("learning.demo.v1", 0, 11L, "station-1", "second"));

        assertThat(messageService.recentMessages(10))
                .extracting(message -> message.payload())
                .containsExactly("second", "first");
    }
}
