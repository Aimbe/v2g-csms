package com.charging.adapter.in.websocket.support.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * OCPP 메시지의 JSON payload를 해당 파라미터 타입으로 역직렬화하여 주입한다.
 * Spring MVC의 @RequestBody와 유사한 역할.
 *
 * 사용 예:
 * <pre>
 * {@literal @}OcppAction("BootNotification")
 * public BootNotificationResponse handle(@OcppPayload BootNotificationRequest request) { ... }
 * </pre>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface OcppPayload {
}
