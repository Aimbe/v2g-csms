package com.charging.domain.enums;

/**
 * OCPP 2.0.1 Connector Status
 * 커넥터의 현재 상태를 나타냅니다.
 */
public enum ConnectorStatusEnum {
    /**
     * 사용 가능 상태
     */
    AVAILABLE,

    /**
     * 점유됨 (차량 연결됨)
     */
    OCCUPIED,

    /**
     * 예약됨
     */
    RESERVED,

    /**
     * 사용 불가
     */
    UNAVAILABLE,

    /**
     * 오류 상태
     */
    FAULTED
}
