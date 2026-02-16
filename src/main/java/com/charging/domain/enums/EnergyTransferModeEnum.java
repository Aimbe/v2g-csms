package com.charging.domain.enums;

/**
 * OCPP 2.1 에너지 전송 모드
 * BPT (Bidirectional Power Transfer) = 양방향 전력 전송 (V2G)
 */
public enum EnergyTransferModeEnum {
    /** DC 단방향 충전 */
    DC,
    /** AC 단상 충전 */
    AC_SINGLE_PHASE,
    /** AC 2상 충전 */
    AC_TWO_PHASE,
    /** AC 3상 충전 */
    AC_THREE_PHASE,
    /** AC 단상 양방향 (V2G) */
    AC_BPT_SINGLE_PHASE,
    /** AC 3상 양방향 (V2G) */
    AC_BPT_THREE_PHASE,
    /** DC 양방향 (V2G) */
    DC_BPT
}
