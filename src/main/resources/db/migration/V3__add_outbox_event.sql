-- ============================================================
-- V3: OUTBOX_EVENT 테이블 추가
-- Transaction 이벤트를 Kafka로 안전하게 relay하기 위한 Outbox
-- ============================================================

CREATE TABLE OUTBOX_EVENT (
    id              BIGINT        NOT NULL AUTO_INCREMENT,
    event_id        VARCHAR(64)   NOT NULL,
    aggregate_type  VARCHAR(50)   NOT NULL,
    aggregate_id    VARCHAR(100)  NOT NULL,
    topic           VARCHAR(100)  NOT NULL,
    event_type      VARCHAR(100)  NOT NULL,
    event_version   INT           NOT NULL,
    partition_key   VARCHAR(100)  NOT NULL,
    payload         LONGTEXT      NOT NULL,
    status          VARCHAR(20)   NOT NULL,
    retry_count     INT           NOT NULL DEFAULT 0,
    occurred_at     DATETIME(6)   NOT NULL,
    published_at    DATETIME(6),
    last_error      VARCHAR(1000),
    created_at      DATETIME(6)   NOT NULL,
    updated_at      DATETIME(6)   NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_outbox_event_id (event_id),
    KEY idx_outbox_status_created (status, created_at),
    KEY idx_outbox_aggregate (aggregate_type, aggregate_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
