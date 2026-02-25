-- ============================================================
-- V2: CHARGING_SCHEDULE_PERIOD 테이블 추가
-- EV 충전 스케줄 기간 저장 (NotifyEVChargingSchedule)
-- ============================================================

CREATE TABLE CHARGING_SCHEDULE_PERIOD (
    id                    BIGINT        NOT NULL AUTO_INCREMENT,
    station_id            VARCHAR(50)   NOT NULL,
    evse_id               INT           NOT NULL,
    charging_schedule_id  INT           NOT NULL,
    charging_rate_unit    VARCHAR(10)   NOT NULL,
    start_period          INT           NOT NULL,
    limit_value           DECIMAL(10,2) NOT NULL,
    number_phases         INT,
    created_at            DATETIME(6)   NOT NULL,
    updated_at            DATETIME(6)   NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_csp_station_evse (station_id, evse_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
