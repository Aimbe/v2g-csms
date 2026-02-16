package com.charging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * OCPP 2.0 충전기 도메인 애플리케이션
 * JPA 학습을 위한 Spring Boot 애플리케이션
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.charging.adapter.out.persistence.repository")
public class ChargingDomainApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChargingDomainApplication.class, args);
    }
}
