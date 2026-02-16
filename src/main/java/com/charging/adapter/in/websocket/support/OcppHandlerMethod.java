package com.charging.adapter.in.websocket.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * OCPP 액션 핸들러 메서드를 래핑하여 ArgumentResolver 체인을 통한 호출을 지원한다.
 * Spring MVC의 InvocableHandlerMethod와 동일한 역할.
 *
 * <p>핸들러 빈과 메서드를 보관하고, 호출 시 파라미터를 resolver 체인으로 해석한 뒤
 * reflection으로 메서드를 invoke한다.</p>
 */
public class OcppHandlerMethod {

    private final Object bean;
    private final Method method;
    private final Parameter[] parameters;

    public OcppHandlerMethod(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
        this.parameters = method.getParameters();
        this.method.setAccessible(true);
    }

    /**
     * ArgumentResolver 체인을 사용하여 파라미터를 해석하고 메서드를 호출한다.
     *
     * @param resolverComposite ArgumentResolver 체인
     * @param context           OCPP 요청 컨텍스트
     * @return 메서드 실행 결과 (응답 객체)
     */
    public Object invoke(OcppHandlerMethodArgumentResolverComposite resolverComposite,
                         OcppActionContext context) throws Exception {
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            args[i] = resolverComposite.resolveArgument(parameters[i], context);
        }

        try {
            return method.invoke(bean, args);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof Exception cause) {
                throw cause;
            }
            throw e;
        }
    }

    public String getBeanName() {
        return bean.getClass().getSimpleName();
    }

    public String getMethodName() {
        return method.getName();
    }

    public Parameter[] getParameters() {
        return parameters;
    }
}
