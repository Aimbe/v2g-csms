-- ============================================================
-- V4: DASHBOARD_PROJECTION 테이블 추가
-- Dashboard CQRS read model
-- ============================================================

CREATE TABLE DASHBOARD_PROJECTION (
    projection_key        VARCHAR(50) NOT NULL,
    total_stations        BIGINT      NOT NULL,
    total_evses           BIGINT      NOT NULL,
    available_connectors  BIGINT      NOT NULL,
    occupied_connectors   BIGINT      NOT NULL,
    active_transactions   BIGINT      NOT NULL,
    today_transactions    BIGINT      NOT NULL,
    created_at            DATETIME(6) NOT NULL,
    updated_at            DATETIME(6) NOT NULL,
    PRIMARY KEY (projection_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
