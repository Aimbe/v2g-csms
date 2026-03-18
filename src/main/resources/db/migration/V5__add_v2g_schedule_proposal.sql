-- ============================================================
-- V5: V2G_SCHEDULE_PROPOSAL 테이블 추가
-- ChargingNeeds 기반의 V2G 오케스트레이션 제안 결과 저장
-- ============================================================

CREATE TABLE V2G_SCHEDULE_PROPOSAL (
    id                        BIGINT         NOT NULL AUTO_INCREMENT,
    station_id                VARCHAR(50)    NOT NULL,
    evse_id                   INT            NOT NULL,
    source_charging_needs_id  BIGINT         NOT NULL,
    requested_energy_transfer VARCHAR(50)    NOT NULL,
    proposal_action           VARCHAR(20)    NOT NULL,
    charging_rate_unit        VARCHAR(10)    NOT NULL,
    proposed_power            DECIMAL(15,2)  NOT NULL,
    target_soc                INT,
    valid_until               DATETIME(6),
    proposal_status           VARCHAR(20)    NOT NULL,
    reason                    VARCHAR(500),
    created_at                DATETIME(6)    NOT NULL,
    updated_at                DATETIME(6)    NOT NULL,
    PRIMARY KEY (id),
    KEY idx_v2g_schedule_station_evse (station_id, evse_id),
    KEY idx_v2g_schedule_source (source_charging_needs_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
