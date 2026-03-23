package com.charging.domain.enums;

/**
 * OCPP 2.0.1 Measurand Type
 * 측정값의 종류를 나타냅니다.
 */
public enum MeasurandEnum {
    /**
     * 유효 전력 수입 (kW)
     */
    POWER_ACTIVE_IMPORT,

    /**
     * 유효 전력 수출 (kW)
     */
    POWER_ACTIVE_EXPORT,

    /**
     * 에너지 수입 (kWh)
     */
    ENERGY_ACTIVE_IMPORT_REGISTER,

    /**
     * 에너지 수출 (kWh)
     */
    ENERGY_ACTIVE_EXPORT_REGISTER,

    /**
     * 무효 전력 수입 (kvarh)
     */
    POWER_REACTIVE_IMPORT,

    /**
     * 전류 (A)
     */
    CURRENT_IMPORT,

    /**
     * 전류 수출 (A)
     */
    CURRENT_EXPORT,

    /**
     * 전압 (V)
     */
    VOLTAGE,

    /**
     * 주파수 (Hz)
     */
    FREQUENCY,

    /**
     * 온도 (Celsius)
     */
    TEMPERATURE,

    /**
     * SoC (State of Charge) %
     */
    SOC,

    /**
     * RPM
     */
    RPM
}
