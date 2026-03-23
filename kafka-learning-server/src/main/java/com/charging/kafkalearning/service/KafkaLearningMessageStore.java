package com.charging.kafkalearning.service;

import com.charging.kafkalearning.model.ConsumedKafkaMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.springframework.stereotype.Component;

@Component
public class KafkaLearningMessageStore {

    private final ConcurrentLinkedDeque<ConsumedKafkaMessage> messages = new ConcurrentLinkedDeque<>();
    private final int historySize;

    public KafkaLearningMessageStore(com.charging.kafkalearning.config.KafkaLearningProperties properties) {
        this.historySize = properties.historySize();
    }

    public void append(ConsumedKafkaMessage message) {
        messages.addFirst(message);
        while (messages.size() > historySize) {
            messages.pollLast();
        }
    }

    public List<ConsumedKafkaMessage> recent(int limit) {
        int safeLimit = Math.max(1, limit);
        List<ConsumedKafkaMessage> result = new ArrayList<>(safeLimit);
        int count = 0;
        for (ConsumedKafkaMessage message : messages) {
            if (count >= safeLimit) {
                break;
            }
            result.add(message);
            count++;
        }
        return result;
    }
}
