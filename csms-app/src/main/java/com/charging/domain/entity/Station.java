package com.charging.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 충전소 (Charging Station) 엔티티
 * OCPP 2.0.1 기반 충전소 정보를 관리합니다.
 *
 * OCPP 2.0.1 3-tier 계층 구조의 최상위:
 * ChargingStation > EVSE > Connector
 */
@Entity
@Table(name = "STATION")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Station extends BaseEntity {

    /**
     * ID (Primary Key)
     * Oracle IDENTITY 컬럼으로 자동 생성
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 충전소 ID (Unique)
     */
    @Column(name = "station_id", length = 50, nullable = false, unique = true)
    private String stationId;

    /**
     * 전력 최대 수용량 (kW)
     */
    @Column(name = "power_grid_capacity", precision = 10, scale = 2, nullable = false)
    private BigDecimal powerGridCapacity;

    /**
     * 최대 허용 가격 (원)
     */
    @Column(name = "max_price_limit", precision = 10, scale = 2, nullable = false)
    private BigDecimal maxPriceLimit;

    /**
     * 스마트충전 알고리즘 모드
     */
    @Column(name = "algorithm_mode", nullable = false)
    private Integer algorithmMode;

    /**
     * 시간확장 계수
     */
    @Column(name = "time_extension_factor", precision = 5, scale = 2, nullable = false)
    private BigDecimal timeExtensionFactor;

    /**
     * 최대 반복 횟수
     */
    @Column(name = "max_iteration_count", nullable = false)
    private Integer maxIterationCount;

    /**
     * 요금 적용 전력 ID
     */
    @Column(name = "billing_power_id", nullable = false)
    private Long billingPowerId;

    /**
     * V2G 지원 여부 (OCPP 2.1)
     */
    @Column(name = "v2g_supported")
    @Builder.Default
    private Boolean v2gSupported = false;

    /**
     * ISO 15118 지원 여부 (OCPP 2.1)
     */
    @Column(name = "iso15118_supported")
    @Builder.Default
    private Boolean iso15118Supported = false;

    /**
     * 충전소에 속한 EVSE 목록
     * 1:N 관계 - 하나의 충전소는 여러 EVSE를 가질 수 있음
     */
    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Evse> evses = new ArrayList<>();

    /**
     * EVSE 추가 헬퍼 메서드
     * 양방향 관계를 편리하게 설정
     */
    public void addEvse(Evse evse) {
        evses.add(evse);
        evse.setStation(this);
    }

    /**
     * EVSE 제거 헬퍼 메서드
     */
    public void removeEvse(Evse evse) {
        evses.remove(evse);
        evse.setStation(null);
    }
}
