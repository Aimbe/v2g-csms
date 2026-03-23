package com.charging.domain.enums;

/**
 * OCPP 2.1 비용 종류
 * ISO 15118-20 기반 비용 유형을 나타냅니다.
 */
public enum CostKindEnum {
    /** CO2 배출량 */
    CARBON_DIOXIDE_EMISSION,
    /** 상대 가격 비율 (%) */
    RELATIVE_PRICE_PERCENTAGE,
    /** 재생에너지 발전 비율 (%) */
    RENEWABLE_GENERATION_PERCENTAGE
}
