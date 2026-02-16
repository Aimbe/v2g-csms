package com.charging.domain.enums;

/**
 * OCPP 2.1 Charging State
 * 충전 세션의 현재 상태를 나타냅니다.
 */
public enum ChargingStateEnum {
    /**
     * 충전 중
     */
    CHARGING,

    /**
     * EV가 연결되었으나 충전은 중지됨
     */
    SUSPENDED_EV,

    /**
     * EVSE가 충전을 중지함
     */
    SUSPENDED_EVSE,

    /**
     * 충전이 완료되었으나 아직 연결됨
     */
    IDLE,

    /**
     * EV가 연결됨 (OCPP 2.1 추가)
     */
    EV_CONNECTED
}
