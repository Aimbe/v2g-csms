package com.charging.domain.entity;

import com.charging.domain.enums.OperationalStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * EVSE (Electric Vehicle Supply Equipment) 엔티티
 * OCPP 2.0.1 기반 충전 장비 정보를 관리합니다.
 *
 * OCPP 2.0.1 3-tier 계층 구조:
 * ChargingStation > EVSE > Connector
 */
@Entity
@Table(
    name = "EVSE",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_evse_station",
            columnNames = {"evse_id", "station_id"}
        )
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Evse extends BaseEntity {

    /**
     * ID (Primary Key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * EVSE ID (OCPP 2.0.1 표준)
     * 충전소 내에서 고유한 EVSE 식별자
     */
    @Column(name = "evse_id", nullable = false)
    private Integer evseId;

    /**
     * 충전소 ID (FK 역할 - 문자열)
     */
    @Column(name = "station_id", length = 50, nullable = false, insertable = false, updatable = false)
    private String stationId;

    /**
     * 최대 허용 전력량 (kW)
     */
    @Column(name = "max_power", precision = 10, scale = 2, nullable = false)
    private BigDecimal maxPower;

    /**
     * 운영 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "operational_status", nullable = false)
    @Builder.Default
    private OperationalStatusEnum operationalStatus = OperationalStatusEnum.OPERATIVE;

    /**
     * 소속 충전소
     * N:1 관계 - 여러 EVSE가 하나의 충전소에 속함
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", referencedColumnName = "station_id", nullable = false)
    private Station station;

    /**
     * EVSE에 속한 커넥터 목록
     * 1:N 관계 - 하나의 EVSE는 여러 커넥터를 가질 수 있음
     */
    @OneToMany(mappedBy = "evse", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Connector> connectors = new ArrayList<>();

    /**
     * EVSE에서 발생한 트랜잭션 목록
     * 1:N 관계 - 하나의 EVSE는 여러 트랜잭션을 가질 수 있음
     */
    @OneToMany(mappedBy = "evse", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();

    /**
     * 충전소 설정 헬퍼 메서드
     * 양방향 관계를 위해 package-private으로 설정
     */
    void setStation(Station station) {
        this.station = station;
    }

    /**
     * 커넥터 추가 헬퍼 메서드
     * 양방향 관계를 편리하게 설정
     */
    public void addConnector(Connector connector) {
        connectors.add(connector);
        connector.setEvse(this);
    }

    /**
     * 커넥터 제거 헬퍼 메서드
     */
    public void removeConnector(Connector connector) {
        connectors.remove(connector);
        connector.setEvse(null);
    }

    /**
     * 트랜잭션 추가 헬퍼 메서드
     */
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        transaction.setEvse(this);
    }
}
