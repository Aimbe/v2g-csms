package com.charging.adapter.in.websocket.support;

import com.charging.adapter.in.websocket.OcppSession;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;

/**
 * OcppSession 타입의 파라미터를 해석한다.
 * 핸들러 메서드에 OcppSession 타입 파라미터가 있으면 현재 세션을 주입한다.
 *
 * Spring MVC에서 HttpServletRequest를 자동 주입하는 것과 동일한 원리.
 */
@Component
public class OcppSessionArgumentResolver implements OcppHandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(Parameter parameter) {
        return OcppSession.class.isAssignableFrom(parameter.getType());
    }

    @Override
    public Object resolveArgument(Parameter parameter, OcppActionContext context) {
        return context.session();
    }
}
