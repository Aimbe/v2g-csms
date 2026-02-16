package com.charging.adapter.in.websocket.support;

import com.charging.adapter.in.websocket.OcppSession;
import com.charging.adapter.in.websocket.model.OcppCall;

/**
 * OCPP 액션 처리 시 필요한 요청 컨텍스트를 담는 객체.
 * Spring MVC의 NativeWebRequest / ServletRequest에 해당.
 *
 * Dispatcher가 생성하여 ArgumentResolver 체인에 전달한다.
 */
public record OcppActionContext(
    OcppCall call,
    OcppSession session,
    String rawPayload
) {
    public String messageId() {
        return call.messageId();
    }

    public String action() {
        return call.action();
    }

    public String stationId() {
        return session.getStationId();
    }
}
