package com.charging.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * 충전 스케줄 기간 엔티티
 * OCPP 2.0.1 NotifyEVChargingSchedule을 통해 수신된 EV 제안 충전 스케줄 기간 정보를 관리합니다.
 */
@Entity
@Table(name = "CHARGING_SCHEDULE_PERIOD")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChargingSchedulePeriod extends BaseEntity {

    /**
     * ID (Primary Key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 충전소 ID
     */
    @Column(name = "station_id", length = 50, nullable = false)
    private String stationId;

    /**
     * EVSE ID
     */
    @Column(name = "evse_id", nullable = false)
    private Integer evseId;

    /**
     * 충전 스케줄 ID (OCPP 표준)
     */
    @Column(name = "charging_schedule_id", nullable = false)
    private Integer chargingScheduleId;

    /**
     * 충전률 단위 (W 또는 A)
     */
    @Column(name = "charging_rate_unit", length = 10, nullable = false)
    private String chargingRateUnit;

    /**
     * 스케줄 시작으로부터 이 기간의 시작 시간 (초)
     */
    @Column(name = "start_period", nullable = false)
    private Integer startPeriod;

    /**
     * 전력/전류 한도 (limit은 SQL 예약어이므로 limit_value 컬럼명 사용)
     */
    @Column(name = "limit_value", precision = 10, scale = 2, nullable = false)
    private BigDecimal limitValue;

    /**
     * 사용 상(Phase) 수 (선택적)
     */
    @Column(name = "number_phases")
    private Integer numberPhases;
}
