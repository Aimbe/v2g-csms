package com.charging.adapter.in.websocket.support.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 현재 WebSocket 세션의 충전소 ID를 String 파라미터에 주입한다.
 * Spring MVC의 @PathVariable("stationId")와 유사한 역할.
 *
 * 사용 예:
 * <pre>
 * {@literal @}OcppAction("Heartbeat")
 * public HeartbeatResponse handle(@StationId String stationId) { ... }
 * </pre>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface StationId {
}
