package com.charging.domain.enums;

/**
 * OCPP 2.1 충전 제한 소스
 * 충전 제한이 어디에서 발생했는지를 나타냅니다.
 */
public enum ChargingLimitSourceEnum {
    /** Energy Management System */
    EMS,
    /** 기타 */
    OTHER,
    /** System Operator */
    SO,
    /** Charging Station Operator */
    CSO
}
