-- ============================================================
-- V1: CSMS 초기 스키마 (OCPP 2.0.1 + 2.1 V2G)
-- MySQL 8.0 / utf8mb4
-- ============================================================

-- 1. STATION (충전소)
CREATE TABLE STATION (
    id            BIGINT        NOT NULL AUTO_INCREMENT,
    station_id    VARCHAR(50)   NOT NULL,
    power_grid_capacity   DECIMAL(10,2) NOT NULL,
    max_price_limit       DECIMAL(10,2) NOT NULL,
    algorithm_mode        INT           NOT NULL,
    time_extension_factor DECIMAL(5,2)  NOT NULL,
    max_iteration_count   INT           NOT NULL,
    billing_power_id      BIGINT        NOT NULL,
    v2g_supported         BIT           DEFAULT 0,
    iso15118_supported    BIT           DEFAULT 0,
    created_at    DATETIME(6)   NOT NULL,
    updated_at    DATETIME(6)   NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_station_station_id (station_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. EVSE (충전 장비)
CREATE TABLE EVSE (
    id                  BIGINT        NOT NULL AUTO_INCREMENT,
    evse_id             INT           NOT NULL,
    station_id          VARCHAR(50)   NOT NULL,
    max_power           DECIMAL(10,2) NOT NULL,
    operational_status  VARCHAR(50)   NOT NULL,
    created_at          DATETIME(6)   NOT NULL,
    updated_at          DATETIME(6)   NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_evse_station (evse_id, station_id),
    CONSTRAINT fk_evse_station
        FOREIGN KEY (station_id) REFERENCES STATION (station_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. CONNECTOR (커넥터)
CREATE TABLE CONNECTOR (
    id            BIGINT        NOT NULL AUTO_INCREMENT,
    connector_id  INT           NOT NULL,
    evse_id       INT           NOT NULL,
    station_id    VARCHAR(50)   NOT NULL,
    max_power     DECIMAL(10,2) NOT NULL,
    min_power     DECIMAL(10,2) NOT NULL,
    status        VARCHAR(50)   NOT NULL,
    created_at    DATETIME(6)   NOT NULL,
    updated_at    DATETIME(6)   NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_connector_evse_station (evse_id, station_id, connector_id),
    CONSTRAINT fk_connector_evse
        FOREIGN KEY (evse_id, station_id) REFERENCES EVSE (evse_id, station_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. TRANSACTION (충전 트랜잭션) - MySQL 예약어이므로 백틱 사용
CREATE TABLE `TRANSACTION` (
    id                  BIGINT        NOT NULL AUTO_INCREMENT,
    transaction_id      VARCHAR(50)   NOT NULL,
    evse_id             INT           NOT NULL,
    station_id          VARCHAR(50)   NOT NULL,
    connector_id        INT           NOT NULL,
    id_token            VARCHAR(36)   NOT NULL,
    event_type          VARCHAR(50)   NOT NULL,
    charging_state      VARCHAR(50),
    start_time          DATETIME(6)   NOT NULL,
    stop_time           DATETIME(6),
    total_energy        DECIMAL(15,3),
    start_meter_value   DECIMAL(15,3),
    stop_meter_value    DECIMAL(15,3),
    stop_reason         VARCHAR(100),
    energy_transfer_mode VARCHAR(50),
    discharged_energy   DECIMAL(15,3),
    v2x_transfer_mode   VARCHAR(50),
    created_at          DATETIME(6)   NOT NULL,
    updated_at          DATETIME(6)   NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_transaction_id (transaction_id),
    CONSTRAINT fk_transaction_evse
        FOREIGN KEY (evse_id, station_id) REFERENCES EVSE (evse_id, station_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. METER_VALUE (미터 측정값)
CREATE TABLE METER_VALUE (
    id              BIGINT        NOT NULL AUTO_INCREMENT,
    transaction_id  BIGINT,
    `timestamp`     DATETIME(6)   NOT NULL,
    measurand       VARCHAR(50)   NOT NULL,
    `value`         DECIMAL(15,3) NOT NULL,
    unit            VARCHAR(20),
    phase           VARCHAR(10),
    location        VARCHAR(20),
    created_at      DATETIME(6)   NOT NULL,
    updated_at      DATETIME(6)   NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_meter_value_transaction
        FOREIGN KEY (transaction_id) REFERENCES `TRANSACTION` (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. CHARGING_PROFILE (충전 프로파일)
CREATE TABLE CHARGING_PROFILE (
    id                       BIGINT        NOT NULL AUTO_INCREMENT,
    charging_profile_id      INT           NOT NULL,
    station_id               VARCHAR(50)   NOT NULL,
    evse_id                  INT,
    transaction_id           VARCHAR(50),
    stack_level              INT           NOT NULL,
    charging_profile_purpose VARCHAR(50)   NOT NULL,
    charging_profile_kind    VARCHAR(50)   NOT NULL,
    valid_from               DATETIME(6),
    valid_to                 DATETIME(6),
    duration                 INT,
    start_schedule           DATETIME(6),
    charging_rate_unit       VARCHAR(10)   NOT NULL,
    min_charging_rate        DECIMAL(10,2),
    is_active                BIT           NOT NULL DEFAULT 1,
    max_discharge_power      DECIMAL(15,2),
    min_discharge_power      DECIMAL(15,2),
    discharge_rate_unit      VARCHAR(10),
    created_at               DATETIME(6)   NOT NULL,
    updated_at               DATETIME(6)   NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_charging_profile_id (charging_profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. CHARGING_NEEDS (EV 충전 요구사항)
CREATE TABLE CHARGING_NEEDS (
    id                        BIGINT        NOT NULL AUTO_INCREMENT,
    station_id                VARCHAR(50)   NOT NULL,
    evse_id                   INT           NOT NULL,
    requested_energy_transfer VARCHAR(50)   NOT NULL,
    departure_time            DATETIME(6),
    ac_max_current            DECIMAL(10,2),
    ac_max_voltage            DECIMAL(10,2),
    ac_min_current            DECIMAL(10,2),
    dc_max_current            DECIMAL(10,2),
    dc_max_voltage            DECIMAL(10,2),
    dc_max_power              DECIMAL(15,2),
    dc_min_current            DECIMAL(10,2),
    dc_min_voltage            DECIMAL(10,2),
    dc_min_power              DECIMAL(15,2),
    dc_target_soc             INT,
    dc_bulk_soc               INT,
    dc_full_soc               INT,
    dc_energy_capacity        DECIMAL(15,2),
    transaction_id            VARCHAR(50),
    created_at                DATETIME(6)   NOT NULL,
    updated_at                DATETIME(6)   NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_charging_needs_transaction
        FOREIGN KEY (transaction_id) REFERENCES `TRANSACTION` (transaction_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
