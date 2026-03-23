package com.charging.domain.enums;

/**
 * V2X 전송 모드
 * Vehicle-to-Everything 양방향 에너지 전송 모드를 나타냅니다.
 */
public enum V2XTransferModeEnum {
    /** Vehicle-to-Grid: 전력망으로 방전 */
    V2G,
    /** Vehicle-to-Home: 가정으로 방전 */
    V2H,
    /** Vehicle-to-Building: 건물로 방전 */
    V2B,
    /** Vehicle-to-Load: 부하로 방전 */
    V2L
}
