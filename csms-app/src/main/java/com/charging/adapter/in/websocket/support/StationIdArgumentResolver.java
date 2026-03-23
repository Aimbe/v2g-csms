package com.charging.adapter.in.websocket.support;

import com.charging.adapter.in.websocket.support.annotation.StationId;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;

/**
 * @StationId 어노테이션이 붙은 String 파라미터에 현재 충전소 ID를 주입한다.
 */
@Component
public class StationIdArgumentResolver implements OcppHandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(Parameter parameter) {
        return parameter.isAnnotationPresent(StationId.class)
                && String.class.isAssignableFrom(parameter.getType());
    }

    @Override
    public Object resolveArgument(Parameter parameter, OcppActionContext context) {
        return context.stationId();
    }
}
