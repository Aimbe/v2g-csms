package com.charging.adapter.in.websocket.support;

import com.charging.adapter.in.websocket.support.annotation.MessageId;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;

/**
 * @MessageId 어노테이션이 붙은 String 파라미터에 OCPP 메시지 ID를 주입한다.
 */
@Component
public class MessageIdArgumentResolver implements OcppHandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(Parameter parameter) {
        return parameter.isAnnotationPresent(MessageId.class)
                && String.class.isAssignableFrom(parameter.getType());
    }

    @Override
    public Object resolveArgument(Parameter parameter, OcppActionContext context) {
        return context.messageId();
    }
}
