package com.charging.domain.enums;

/**
 * OCPP 2.1 Charging Profile Purpose Type
 * 충전 프로파일의 목적을 나타냅니다.
 */
public enum ChargingProfilePurposeEnum {
    /**
     * 충전소 최대 전력 제한
     */
    CHARGE_POINT_MAX_PROFILE,

    /**
     * TX 기본 프로파일 (트랜잭션 기본)
     */
    TX_DEFAULT_PROFILE,

    /**
     * TX 프로파일 (특정 트랜잭션)
     */
    TX_PROFILE,

    /**
     * TX 방전 프로파일 (V2G 방전용, OCPP 2.1 추가)
     */
    TX_DISCHARGE_PROFILE
}
