package com.charging.domain.entity;

import com.charging.domain.enums.MeasurandEnum;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 미터 값 엔티티
 * OCPP 2.0.1 기반 측정값 정보를 관리합니다.
 */
@Entity
@Table(name = "METER_VALUE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MeterValue extends BaseEntity {

    /**
     * ID (Primary Key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 트랜잭션 ID (FK)
     */
    @Column(name = "transaction_id", insertable = false, updatable = false)
    private Long transactionIdFk;

    /**
     * 측정 시간
     */
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    /**
     * 측정값 종류
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "measurand", nullable = false)
    private MeasurandEnum measurand;

    /**
     * 측정값
     */
    @Column(name = "value", precision = 15, scale = 3, nullable = false)
    private BigDecimal value;

    /**
     * 단위
     */
    @Column(name = "unit", length = 20)
    private String unit;

    /**
     * Phase (전기 위상)
     */
    @Column(name = "phase", length = 10)
    private String phase;

    /**
     * 위치 (Inlet, Outlet, Body 등)
     */
    @Column(name = "location", length = 20)
    private String location;

    /**
     * 소속 트랜잭션
     * N:1 관계
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    /**
     * 트랜잭션 설정 헬퍼 메서드
     */
    void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}
