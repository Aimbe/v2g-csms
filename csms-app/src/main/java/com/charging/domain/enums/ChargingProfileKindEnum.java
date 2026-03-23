package com.charging.domain.enums;

/**
 * OCPP 2.0.1 Charging Profile Kind Type
 * 충전 프로파일의 종류를 나타냅니다.
 */
public enum ChargingProfileKindEnum {
    /**
     * 절대 시간 기반
     */
    ABSOLUTE,

    /**
     * 반복적 (매일, 매주 등)
     */
    RECURRING,

    /**
     * 상대적 (트랜잭션 시작 시점 기준)
     */
    RELATIVE
}
