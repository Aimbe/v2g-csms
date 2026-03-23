package com.charging.kafkalearning.controller;

import com.charging.kafkalearning.controller.dto.ConsumedKafkaMessageResponse;
import com.charging.kafkalearning.controller.dto.KafkaTopicsResponse;
import com.charging.kafkalearning.controller.dto.PublishKafkaMessageRequest;
import com.charging.kafkalearning.controller.dto.PublishedKafkaMessageResponse;
import com.charging.kafkalearning.model.ConsumedKafkaMessage;
import com.charging.kafkalearning.model.PublishedKafkaMessage;
import com.charging.kafkalearning.service.KafkaLearningMessageService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/learning/kafka")
public class KafkaLearningController {

    private final KafkaLearningMessageService messageService;

    public KafkaLearningController(KafkaLearningMessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/topics")
    public KafkaTopicsResponse topics() {
        return new KafkaTopicsResponse(messageService.defaultTopic(), messageService.topics());
    }

    @PostMapping("/messages")
    public PublishedKafkaMessageResponse publish(@Valid @RequestBody PublishKafkaMessageRequest request) {
        PublishedKafkaMessage publishedMessage = messageService.publish(request.topic(), request.key(), request.payload());
        return new PublishedKafkaMessageResponse(
                publishedMessage.topic(),
                publishedMessage.partition(),
                publishedMessage.offset(),
                publishedMessage.key(),
                publishedMessage.payload()
        );
    }

    @GetMapping("/messages")
    public List<ConsumedKafkaMessageResponse> recentMessages(@RequestParam(defaultValue = "20") int limit) {
        return messageService.recentMessages(limit).stream()
                .map(this::toResponse)
                .toList();
    }

    private ConsumedKafkaMessageResponse toResponse(ConsumedKafkaMessage message) {
        return new ConsumedKafkaMessageResponse(
                message.topic(),
                message.partition(),
                message.offset(),
                message.key(),
                message.payload(),
                message.receivedAt()
        );
    }
}
