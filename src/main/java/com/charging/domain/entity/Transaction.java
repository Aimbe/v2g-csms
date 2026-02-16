package com.charging.domain.entity;

import com.charging.domain.enums.ChargingStateEnum;
import com.charging.domain.enums.EnergyTransferModeEnum;
import com.charging.domain.enums.TransactionEventEnum;
import com.charging.domain.enums.V2XTransferModeEnum;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 트랜잭션 (충전 세션) 엔티티
 * OCPP 2.0.1 기반 충전 트랜잭션 정보를 관리합니다.
 */
@Entity
@Table(name = "TRANSACTION")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Transaction extends BaseEntity {

    /**
     * ID (Primary Key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 트랜잭션 ID (OCPP 표준)
     */
    @Column(name = "transaction_id", length = 50, nullable = false, unique = true)
    private String transactionId;

    /**
     * EVSE ID (FK 역할)
     */
    @Column(name = "evse_id", nullable = false, insertable = false, updatable = false)
    private Integer evseId;

    /**
     * 충전소 ID (FK 역할)
     */
    @Column(name = "station_id", length = 50, nullable = false)
    private String stationId;

    /**
     * 커넥터 ID
     */
    @Column(name = "connector_id", nullable = false)
    private Integer connectorId;

    /**
     * ID Token (인증 토큰)
     */
    @Column(name = "id_token", length = 36, nullable = false)
    private String idToken;

    /**
     * 트랜잭션 이벤트 유형 (현재 상태)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private TransactionEventEnum eventType;

    /**
     * 충전 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "charging_state")
    private ChargingStateEnum chargingState;

    /**
     * 시작 시간
     */
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /**
     * 종료 시간
     */
    @Column(name = "stop_time")
    private LocalDateTime stopTime;

    /**
     * 누적 에너지 (kWh)
     */
    @Column(name = "total_energy", precision = 15, scale = 3)
    private BigDecimal totalEnergy;

    /**
     * 시작 시점 미터 값 (Wh)
     */
    @Column(name = "start_meter_value", precision = 15, scale = 3)
    private BigDecimal startMeterValue;

    /**
     * 종료 시점 미터 값 (Wh)
     */
    @Column(name = "stop_meter_value", precision = 15, scale = 3)
    private BigDecimal stopMeterValue;

    /**
     * 정지 이유
     */
    @Column(name = "stop_reason", length = 100)
    private String stopReason;

    /**
     * 에너지 전송 모드 (OCPP 2.1)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "energy_transfer_mode")
    private EnergyTransferModeEnum energyTransferMode;

    /**
     * 방전 에너지 (kWh, V2G)
     */
    @Column(name = "discharged_energy", precision = 15, scale = 3)
    private BigDecimal dischargedEnergy;

    /**
     * V2X 전송 모드 (nullable)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "v2x_transfer_mode")
    private V2XTransferModeEnum v2xTransferMode;

    /**
     * 소속 EVSE
     * N:1 관계
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "evse_id", referencedColumnName = "evse_id", nullable = false),
        @JoinColumn(name = "station_id", referencedColumnName = "station_id", nullable = false)
    })
    private Evse evse;

    /**
     * 트랜잭션의 미터 값 목록
     * 1:N 관계
     */
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MeterValue> meterValues = new ArrayList<>();

    /**
     * EVSE 설정 헬퍼 메서드
     */
    void setEvse(Evse evse) {
        this.evse = evse;
    }

    /**
     * 미터 값 추가 헬퍼 메서드
     */
    public void addMeterValue(MeterValue meterValue) {
        meterValues.add(meterValue);
        meterValue.setTransaction(this);
    }

    /**
     * 트랜잭션 시작
     */
    public void start(String idToken, LocalDateTime startTime) {
        this.idToken = idToken;
        this.startTime = startTime;
        this.eventType = TransactionEventEnum.STARTED;
        this.chargingState = ChargingStateEnum.IDLE;
    }

    /**
     * 트랜잭션 종료
     */
    public void stop(LocalDateTime stopTime, String stopReason) {
        this.stopTime = stopTime;
        this.stopReason = stopReason;
        this.eventType = TransactionEventEnum.ENDED;
    }

    /**
     * 충전 상태 업데이트
     */
    public void updateChargingState(ChargingStateEnum newState) {
        this.chargingState = newState;
        this.eventType = TransactionEventEnum.UPDATED;
    }

    /**
     * 누적 에너지 계산
     */
    public void calculateTotalEnergy() {
        if (startMeterValue != null && stopMeterValue != null) {
            this.totalEnergy = stopMeterValue.subtract(startMeterValue)
                .divide(new BigDecimal("1000"), 3, BigDecimal.ROUND_HALF_UP); // Wh to kWh
        }
    }
}
