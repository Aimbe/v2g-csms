package com.charging.adapter.in.websocket.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;
import java.util.List;

/**
 * 여러 OcppHandlerMethodArgumentResolver를 체인으로 묶어 순서대로 탐색한다.
 * Spring MVC의 HandlerMethodArgumentResolverComposite와 동일한 역할.
 *
 * <p>등록된 resolver 목록을 순회하면서 supportsParameter()가 true인
 * 첫 번째 resolver에게 해석을 위임한다.</p>
 */
@Slf4j
@Component
public class OcppHandlerMethodArgumentResolverComposite {

    private final List<OcppHandlerMethodArgumentResolver> resolvers;

    public OcppHandlerMethodArgumentResolverComposite(List<OcppHandlerMethodArgumentResolver> resolvers) {
        this.resolvers = resolvers;
        log.info("OCPP ArgumentResolver {} 개 등록: {}", resolvers.size(),
                resolvers.stream().map(r -> r.getClass().getSimpleName()).toList());
    }

    /**
     * 주어진 파라미터를 해석할 수 있는 resolver를 찾아 값을 반환한다.
     *
     * @param parameter 핸들러 메서드의 파라미터
     * @param context   OCPP 요청 컨텍스트
     * @return 해석된 파라미터 값
     * @throws IllegalStateException 해석 가능한 resolver가 없는 경우
     */
    public Object resolveArgument(Parameter parameter, OcppActionContext context) {
        for (OcppHandlerMethodArgumentResolver resolver : resolvers) {
            if (resolver.supportsParameter(parameter)) {
                Object resolved = resolver.resolveArgument(parameter, context);
                log.debug("파라미터 해석: {} → {} (resolver={})",
                        parameter.getName(), resolved != null ? resolved.getClass().getSimpleName() : "null",
                        resolver.getClass().getSimpleName());
                return resolved;
            }
        }
        throw new IllegalStateException(
                String.format("파라미터를 해석할 수 있는 ArgumentResolver가 없습니다: %s %s",
                        parameter.getType().getSimpleName(), parameter.getName()));
    }
}
