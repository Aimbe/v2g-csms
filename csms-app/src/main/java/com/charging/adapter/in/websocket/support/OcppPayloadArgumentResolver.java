package com.charging.adapter.in.websocket.support;

import com.charging.adapter.in.websocket.OcppMessageParser;
import com.charging.adapter.in.websocket.OcppSession;
import com.charging.adapter.in.websocket.support.annotation.MessageId;
import com.charging.adapter.in.websocket.support.annotation.OcppPayload;
import com.charging.adapter.in.websocket.support.annotation.StationId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;

/**
 * OCPP JSON payload를 핸들러 메서드의 파라미터 타입으로 역직렬화한다.
 * Spring MVC의 RequestResponseBodyMethodProcessor(@RequestBody 처리)와 동일한 역할.
 *
 * <p>다음 두 가지 조건 중 하나를 만족하면 payload로 판단한다:</p>
 * <ul>
 *   <li>@OcppPayload 어노테이션이 명시적으로 붙은 파라미터</li>
 *   <li>다른 어노테이션(@MessageId, @StationId)이 없고, OcppSession 타입도 아닌 파라미터
 *       (Convention over Configuration)</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class OcppPayloadArgumentResolver implements OcppHandlerMethodArgumentResolver {

    private final OcppMessageParser messageParser;

    @Override
    public boolean supportsParameter(Parameter parameter) {
        // 명시적 @OcppPayload 어노테이션
        if (parameter.isAnnotationPresent(OcppPayload.class)) {
            return true;
        }

        // Convention: 다른 어노테이션이 없고, OcppSession도 아닌 경우 → payload로 간주
        return !parameter.isAnnotationPresent(MessageId.class)
                && !parameter.isAnnotationPresent(StationId.class)
                && !OcppSession.class.isAssignableFrom(parameter.getType());
    }

    @Override
    public Object resolveArgument(Parameter parameter, OcppActionContext context) {
        return messageParser.deserializePayload(context.rawPayload(), parameter.getType());
    }
}
