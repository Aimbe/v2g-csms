package com.charging.adapter.in.websocket.support.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * OCPP 메시지의 messageId를 String 파라미터에 주입한다.
 * Spring MVC의 @PathVariable과 유사한 역할.
 *
 * 사용 예:
 * <pre>
 * {@literal @}OcppAction("BootNotification")
 * public BootNotificationResponse handle(@MessageId String messageId, @OcppPayload BootNotificationRequest request) { ... }
 * </pre>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageId {
}
