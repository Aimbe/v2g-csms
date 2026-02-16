package com.charging.adapter.in.websocket;

import com.charging.adapter.in.websocket.model.OcppCall;
import com.charging.adapter.in.websocket.model.OcppCallError;
import com.charging.adapter.in.websocket.model.OcppCallResult;
import com.charging.adapter.in.websocket.model.OcppErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class OcppActionDispatcher {

    private final ApplicationContext applicationContext;
    private final OcppMessageParser messageParser;
    private final Map<String, ActionHandlerInfo> actionHandlers = new HashMap<>();

    public OcppActionDispatcher(ApplicationContext applicationContext, OcppMessageParser messageParser) {
        this.applicationContext = applicationContext;
        this.messageParser = messageParser;
    }

    @PostConstruct
    public void init() {
        Map<String, Object> beans = applicationContext.getBeansOfType(Object.class);
        for (Object bean : beans.values()) {
            for (Method method : bean.getClass().getDeclaredMethods()) {
                OcppAction annotation = method.getAnnotation(OcppAction.class);
                if (annotation != null) {
                    String actionName = annotation.value();
                    actionHandlers.put(actionName, new ActionHandlerInfo(bean, method));
                    log.info("OCPP 액션 핸들러 등록: action={}, handler={}.{}",
                            actionName, bean.getClass().getSimpleName(), method.getName());
                }
            }
        }
        log.info("총 {} 개의 OCPP 액션 핸들러 등록 완료", actionHandlers.size());
    }

    public String dispatch(OcppCall call, OcppSession session) {
        String action = call.action();
        ActionHandlerInfo handlerInfo = actionHandlers.get(action);

        if (handlerInfo == null) {
            log.warn("처리할 수 없는 OCPP 액션: {}", action);
            OcppCallError error = new OcppCallError(
                    call.messageId(),
                    OcppErrorCode.NOT_IMPLEMENTED,
                    "Action not implemented: " + action,
                    null
            );
            return messageParser.serializeCallError(error);
        }

        try {
            Method method = handlerInfo.method();
            Parameter[] parameters = method.getParameters();

            Object[] args = resolveArguments(parameters, call.payload(), session);

            Object result = method.invoke(handlerInfo.bean(), args);

            String responsePayload = messageParser.serializePayload(result);
            OcppCallResult callResult = new OcppCallResult(call.messageId(), responsePayload);
            return messageParser.serializeCallResult(callResult);

        } catch (Exception e) {
            log.error("OCPP 액션 처리 중 오류: action={}, messageId={}", action, call.messageId(), e);
            OcppCallError error = new OcppCallError(
                    call.messageId(),
                    OcppErrorCode.INTERNAL_ERROR,
                    "Internal error processing " + action + ": " + e.getMessage(),
                    null
            );
            return messageParser.serializeCallError(error);
        }
    }

    private Object[] resolveArguments(Parameter[] parameters, String payload, OcppSession session) {
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Class<?> paramType = parameters[i].getType();
            if (OcppSession.class.isAssignableFrom(paramType)) {
                args[i] = session;
            } else {
                args[i] = messageParser.deserializePayload(payload, paramType);
            }
        }
        return args;
    }

    private record ActionHandlerInfo(Object bean, Method method) {}
}
