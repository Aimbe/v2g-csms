package com.charging.domain.entity;

import com.charging.domain.enums.OutboxStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "OUTBOX_EVENT",
    indexes = {
        @Index(name = "idx_outbox_status_created", columnList = "status, created_at"),
        @Index(name = "idx_outbox_aggregate", columnList = "aggregate_type, aggregate_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_outbox_event_id", columnNames = "event_id")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OutboxEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "event_id", length = 64, nullable = false)
    private String eventId;

    @Column(name = "aggregate_type", length = 50, nullable = false)
    private String aggregateType;

    @Column(name = "aggregate_id", length = 100, nullable = false)
    private String aggregateId;

    @Column(name = "topic", length = 100, nullable = false)
    private String topic;

    @Column(name = "event_type", length = 100, nullable = false)
    private String eventType;

    @Column(name = "event_version", nullable = false)
    private Integer eventVersion;

    @Column(name = "partition_key", length = 100, nullable = false)
    private String partitionKey;

    @Lob
    @Column(name = "payload", nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @Builder.Default
    private OutboxStatusEnum status = OutboxStatusEnum.INIT;

    @Column(name = "retry_count", nullable = false)
    @Builder.Default
    private Integer retryCount = 0;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "last_error", length = 1000)
    private String lastError;

    public void markPublished(LocalDateTime publishedAt) {
        this.status = OutboxStatusEnum.PUBLISHED;
        this.publishedAt = publishedAt;
        this.lastError = null;
    }

    public void recordFailure(String errorMessage, int maxRetryCount) {
        this.retryCount += 1;
        this.lastError = errorMessage;

        if (this.retryCount >= maxRetryCount) {
            this.status = OutboxStatusEnum.FAILED;
        } else {
            this.status = OutboxStatusEnum.INIT;
        }
    }
}
