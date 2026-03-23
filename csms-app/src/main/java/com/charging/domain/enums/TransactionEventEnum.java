package com.charging.domain.enums;

/**
 * OCPP 2.0.1 Transaction Event Type
 * 트랜잭션 이벤트의 유형을 나타냅니다.
 */
public enum TransactionEventEnum {
    /**
     * 트랜잭션이 시작됨
     */
    STARTED,

    /**
     * 트랜잭션이 업데이트됨 (진행 중)
     */
    UPDATED,

    /**
     * 트랜잭션이 종료됨
     */
    ENDED
}
