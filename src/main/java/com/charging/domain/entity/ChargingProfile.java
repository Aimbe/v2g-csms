package com.charging.domain.entity;

import com.charging.domain.enums.ChargingProfileKindEnum;
import com.charging.domain.enums.ChargingProfilePurposeEnum;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 충전 프로파일 엔티티
 * OCPP 2.0.1 기반 스마트 충전 프로파일 정보를 관리합니다.
 */
@Entity
@Table(name = "CHARGING_PROFILE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChargingProfile extends BaseEntity {

    /**
     * ID (Primary Key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 충전 프로파일 ID (OCPP 표준)
     */
    @Column(name = "charging_profile_id", nullable = false, unique = true)
    private Integer chargingProfileId;

    /**
     * 충전소 ID
     */
    @Column(name = "station_id", length = 50, nullable = false)
    private String stationId;

    /**
     * EVSE ID (선택적)
     */
    @Column(name = "evse_id")
    private Integer evseId;

    /**
     * 트랜잭션 ID (선택적)
     */
    @Column(name = "transaction_id", length = 50)
    private String transactionId;

    /**
     * 스택 레벨 (우선순위)
     */
    @Column(name = "stack_level", nullable = false)
    private Integer stackLevel;

    /**
     * 프로파일 목적
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "charging_profile_purpose", nullable = false)
    private ChargingProfilePurposeEnum chargingProfilePurpose;

    /**
     * 프로파일 종류
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "charging_profile_kind", nullable = false)
    private ChargingProfileKindEnum chargingProfileKind;

    /**
     * 유효 시작 시간
     */
    @Column(name = "valid_from")
    private LocalDateTime validFrom;

    /**
     * 유효 종료 시간
     */
    @Column(name = "valid_to")
    private LocalDateTime validTo;

    /**
     * 충전 스케줄 기간 (초)
     */
    @Column(name = "duration")
    private Integer duration;

    /**
     * 시작 스케줄
     */
    @Column(name = "start_schedule")
    private LocalDateTime startSchedule;

    /**
     * 충전률 단위 (W 또는 A)
     */
    @Column(name = "charging_rate_unit", length = 10, nullable = false)
    private String chargingRateUnit;

    /**
     * 최소 충전률
     */
    @Column(name = "min_charging_rate", precision = 10, scale = 2)
    private BigDecimal minChargingRate;

    /**
     * 프로파일 활성화 여부
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * 최대 방전 전력 (W, V2G용)
     */
    @Column(name = "max_discharge_power", precision = 15, scale = 2)
    private BigDecimal maxDischargePower;

    /**
     * 최소 방전 전력 (W, V2G용)
     */
    @Column(name = "min_discharge_power", precision = 15, scale = 2)
    private BigDecimal minDischargePower;

    /**
     * 방전률 단위 (W 또는 A)
     */
    @Column(name = "discharge_rate_unit", length = 10)
    private String dischargeRateUnit;

    /**
     * 프로파일 활성화/비활성화
     */
    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }
}
