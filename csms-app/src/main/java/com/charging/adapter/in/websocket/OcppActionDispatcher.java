package com.charging.adapter.in.websocket;

import com.charging.adapter.in.websocket.model.OcppCall;
import com.charging.adapter.in.websocket.model.OcppCallError;
import com.charging.adapter.in.websocket.model.OcppCallResult;
import com.charging.adapter.in.websocket.model.OcppErrorCode;
import com.charging.adapter.in.websocket.support.OcppActionContext;
import com.charging.adapter.in.websocket.support.OcppHandlerMethod;
import com.charging.adapter.in.websocket.support.OcppHandlerMethodArgumentResolverComposite;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * OCPP 액션 디스패처.
 * Spring MVC의 DispatcherServlet + RequestMappingHandlerAdapter 역할을 결합한 핵심 컴포넌트.
 *
 * <p>동작 흐름:</p>
 * <pre>
 * 1. @PostConstruct: 모든 Spring Bean을 스캔하여 @OcppAction 메서드를 OcppHandlerMethod로 등록
 * 2. dispatch(): OcppCall 수신 시
 *    a. action명으로 OcppHandlerMethod 조회 (HandlerMapping 역할)
 *    b. OcppActionContext 생성 (요청 컨텍스트)
 *    c. OcppHandlerMethod.invoke() → ArgumentResolverComposite로 파라미터 해석 후 실행
 *    d. 결과를 OcppCallResult로 직렬화하여 반환
 * </pre>
 *
 * <p>Spring MVC와의 대응 관계:</p>
 * <pre>
 * Spring MVC                        │ OCPP Mini Framework
 * ────────────────────────────────── │ ──────────────────────────────────
 * DispatcherServlet                  │ OcppActionDispatcher
 * RequestMappingHandlerMapping       │ @OcppAction → OcppHandlerMethod Map
 * InvocableHandlerMethod             │ OcppHandlerMethod
 * HandlerMethodArgumentResolver      │ OcppHandlerMethodArgumentResolver
 * HandlerMethodArgumentResolverCompo │ OcppHandlerMethodArgumentResolverComposite
 * @RequestMapping                    │ @OcppAction
 * @RequestBody                       │ @OcppPayload
 * @PathVariable                      │ @StationId, @MessageId
 * NativeWebRequest                   │ OcppActionContext
 * </pre>
 */
@Slf4j
@Component
public class OcppActionDispatcher {

    private final ApplicationContext applicationContext;
    private final OcppMessageParser messageParser;
    private final OcppHandlerMethodArgumentResolverComposite resolverComposite;
    private final Map<String, OcppHandlerMethod> actionHandlers = new HashMap<>();

    public OcppActionDispatcher(ApplicationContext applicationContext,
                                OcppMessageParser messageParser,
                                OcppHandlerMethodArgumentResolverComposite resolverComposite) {
        this.applicationContext = applicationContext;
        this.messageParser = messageParser;
        this.resolverComposite = resolverComposite;
    }

    /**
     * 애플리케이션 시작 시 모든 Spring Bean을 스캔하여 @OcppAction 핸들러를 등록한다.
     * Spring MVC의 RequestMappingHandlerMapping.afterPropertiesSet()과 동일한 역할.
     */
    @PostConstruct
    public void init() {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);
            Class<?> beanClass = bean.getClass();

            for (Method method : beanClass.getDeclaredMethods()) {
                OcppAction annotation = method.getAnnotation(OcppAction.class);
                if (annotation != null) {
                    String actionName = annotation.value();
                    OcppHandlerMethod handlerMethod = new OcppHandlerMethod(bean, method);
                    actionHandlers.put(actionName, handlerMethod);
                    log.info("OCPP 액션 핸들러 등록: action={}, handler={}.{}({})",
                            actionName, handlerMethod.getBeanName(), handlerMethod.getMethodName(),
                            formatParameterTypes(handlerMethod));
                }
            }
        }
        log.info("총 {} 개의 OCPP 액션 핸들러 등록 완료", actionHandlers.size());
    }

    /**
     * OCPP CALL 메시지를 처리하고 CALLRESULT 또는 CALLERROR를 반환한다.
     * Spring MVC의 DispatcherServlet.doDispatch()와 동일한 역할.
     */
    public String dispatch(OcppCall call, OcppSession session) {
        String action = call.action();
        OcppHandlerMethod handlerMethod = actionHandlers.get(action);

        if (handlerMethod == null) {
            log.warn("처리할 수 없는 OCPP 액션: {}", action);
            return serializeError(call.messageId(), OcppErrorCode.NOT_IMPLEMENTED,
                    "Action not implemented: " + action);
        }

        try {
            // 1. 요청 컨텍스트 생성
            OcppActionContext context = new OcppActionContext(call, session, call.payload());

            // 2. ArgumentResolver 체인을 통한 파라미터 해석 + 메서드 호출
            Object result = handlerMethod.invoke(resolverComposite, context);

            // 3. 응답 직렬화
            String responsePayload = messageParser.serializePayload(result);
            OcppCallResult callResult = new OcppCallResult(call.messageId(), responsePayload);
            return messageParser.serializeCallResult(callResult);

        } catch (Exception e) {
            log.error("OCPP 액션 처리 중 오류: action={}, messageId={}", action, call.messageId(), e);
            return serializeError(call.messageId(), OcppErrorCode.INTERNAL_ERROR,
                    "Internal error processing " + action + ": " + e.getMessage());
        }
    }

    private String serializeError(String messageId, OcppErrorCode errorCode, String description) {
        OcppCallError error = new OcppCallError(messageId, errorCode, description, null);
        return messageParser.serializeCallError(error);
    }

    private String formatParameterTypes(OcppHandlerMethod handlerMethod) {
        var params = handlerMethod.getParameters();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.length; i++) {
            if (i > 0) sb.append(", ");
            // Check for annotations
            var annotations = params[i].getAnnotations();
            for (var ann : annotations) {
                sb.append("@").append(ann.annotationType().getSimpleName()).append(" ");
            }
            sb.append(params[i].getType().getSimpleName());
        }
        return sb.toString();
    }
}
