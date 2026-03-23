package com.charging.domain.entity;

import com.charging.domain.enums.ConnectorStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * 커넥터 엔티티
 * OCPP 2.0.1 기반 커넥터 정보를 관리합니다.
 *
 * OCPP 2.0.1 3-tier 계층 구조의 최하위:
 * ChargingStation > EVSE > Connector
 */
@Entity
@Table(
    name = "CONNECTOR",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_connector_evse_station",
            columnNames = {"evse_id", "station_id", "connector_id"}
        )
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Connector extends BaseEntity {

    /**
     * ID (Primary Key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 커넥터 ID
     * EVSE 내에서 고유한 커넥터 식별자
     */
    @Column(name = "connector_id", nullable = false)
    private Integer connectorId;

    /**
     * EVSE ID (FK 역할)
     */
    @Column(name = "evse_id", nullable = false, insertable = false, updatable = false)
    private Integer evseId;

    /**
     * 충전소 ID (FK 역할 - 문자열)
     */
    @Column(name = "station_id", length = 50, nullable = false)
    private String stationId;

    /**
     * 최대 허용 전력량 (kW)
     */
    @Column(name = "max_power", precision = 10, scale = 2, nullable = false)
    private BigDecimal maxPower;

    /**
     * 최소 허용 전력량 (kW)
     */
    @Column(name = "min_power", precision = 10, scale = 2, nullable = false)
    private BigDecimal minPower;

    /**
     * 커넥터 현재 상태
     * OCPP 2.0.1 ConnectorStatus
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ConnectorStatusEnum status = ConnectorStatusEnum.AVAILABLE;

    /**
     * 소속 EVSE
     * N:1 관계 - 여러 커넥터가 하나의 EVSE에 속함
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "evse_id", referencedColumnName = "evse_id", nullable = false),
        @JoinColumn(name = "station_id", referencedColumnName = "station_id", nullable = false)
    })
    private Evse evse;

    /**
     * EVSE 설정 헬퍼 메서드
     * 양방향 관계를 위해 package-private으로 설정
     */
    void setEvse(Evse evse) {
        this.evse = evse;
    }

    /**
     * 커넥터 상태 변경
     */
    public void updateStatus(ConnectorStatusEnum newStatus) {
        this.status = newStatus;
    }
}
