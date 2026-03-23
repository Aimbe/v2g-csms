package com.charging.kafkalearning;

import com.charging.kafkalearning.config.KafkaLearningProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
@EnableConfigurationProperties(KafkaLearningProperties.class)
public class KafkaLearningServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaLearningServerApplication.class, args);
    }
}
