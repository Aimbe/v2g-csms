package com.charging.domain.entity;

import com.charging.domain.enums.EnergyTransferModeEnum;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * EV 충전/방전 요구사항 엔티티
 * OCPP 2.1 + ISO 15118-20 기반 EV의 충전 요구사항을 관리합니다.
 */
@Entity
@Table(name = "CHARGING_NEEDS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChargingNeeds extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** 충전소 ID */
    @Column(name = "station_id", length = 50, nullable = false)
    private String stationId;

    /** EVSE ID */
    @Column(name = "evse_id", nullable = false)
    private Integer evseId;

    /** 요청된 에너지 전송 모드 */
    @Enumerated(EnumType.STRING)
    @Column(name = "requested_energy_transfer", nullable = false)
    private EnergyTransferModeEnum requestedEnergyTransfer;

    /** 출발 예정 시간 */
    @Column(name = "departure_time")
    private LocalDateTime departureTime;

    // === AC 충전 파라미터 ===

    /** AC 최대 전류 (A) */
    @Column(name = "ac_max_current", precision = 10, scale = 2)
    private BigDecimal acMaxCurrent;

    /** AC 최대 전압 (V) */
    @Column(name = "ac_max_voltage", precision = 10, scale = 2)
    private BigDecimal acMaxVoltage;

    /** AC 최소 전류 (A) */
    @Column(name = "ac_min_current", precision = 10, scale = 2)
    private BigDecimal acMinCurrent;

    // === DC 충전 파라미터 ===

    /** DC 최대 전류 (A) */
    @Column(name = "dc_max_current", precision = 10, scale = 2)
    private BigDecimal dcMaxCurrent;

    /** DC 최대 전압 (V) */
    @Column(name = "dc_max_voltage", precision = 10, scale = 2)
    private BigDecimal dcMaxVoltage;

    /** DC 최대 전력 (W) */
    @Column(name = "dc_max_power", precision = 15, scale = 2)
    private BigDecimal dcMaxPower;

    /** DC 최소 전류 (A) */
    @Column(name = "dc_min_current", precision = 10, scale = 2)
    private BigDecimal dcMinCurrent;

    /** DC 최소 전압 (V) */
    @Column(name = "dc_min_voltage", precision = 10, scale = 2)
    private BigDecimal dcMinVoltage;

    /** DC 최소 전력 (W) */
    @Column(name = "dc_min_power", precision = 15, scale = 2)
    private BigDecimal dcMinPower;

    // === 배터리 파라미터 ===

    /** 목표 충전율 (%) */
    @Column(name = "dc_target_soc")
    private Integer dcTargetSoc;

    /** 벌크 충전율 (%) */
    @Column(name = "dc_bulk_soc")
    private Integer dcBulkSoc;

    /** 완전 충전율 (%) */
    @Column(name = "dc_full_soc")
    private Integer dcFullSoc;

    /** 배터리 에너지 용량 (Wh) */
    @Column(name = "dc_energy_capacity", precision = 15, scale = 2)
    private BigDecimal dcEnergyCapacity;

    // === 연관관계 ===

    /** 관련 트랜잭션 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", referencedColumnName = "transaction_id")
    private Transaction transaction;
}
