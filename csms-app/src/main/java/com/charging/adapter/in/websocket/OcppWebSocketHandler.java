package com.charging.adapter.in.websocket;

import com.charging.adapter.in.websocket.model.OcppCall;
import com.charging.adapter.in.websocket.model.OcppCallError;
import com.charging.adapter.in.websocket.model.OcppErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;

@Slf4j
@Component
@RequiredArgsConstructor
public class OcppWebSocketHandler extends TextWebSocketHandler {

    private final OcppMessageParser messageParser;
    private final OcppActionDispatcher actionDispatcher;
    private final OcppSessionManager sessionManager;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String stationId = extractStationId(session);
        sessionManager.register(stationId, session);
        log.info("OCPP WebSocket 연결 수립: stationId={}, sessionId={}", stationId, session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        String stationId = extractStationId(session);

        log.debug("OCPP 메시지 수신: stationId={}, message={}", stationId, payload);

        try {
            int messageType = messageParser.getMessageType(payload);

            if (messageType == OcppCall.MESSAGE_TYPE) {
                OcppCall call = messageParser.parseCall(payload);
                OcppSession ocppSession = sessionManager.getSession(stationId)
                        .orElseThrow(() -> new IllegalStateException("Session not found: " + stationId));

                String response = actionDispatcher.dispatch(call, ocppSession);
                session.sendMessage(new TextMessage(response));

                log.debug("OCPP 응답 전송: stationId={}, response={}", stationId, response);
            } else {
                log.warn("지원하지 않는 메시지 타입: type={}, stationId={}", messageType, stationId);
            }

        } catch (Exception e) {
            log.error("OCPP 메시지 처리 오류: stationId={}", stationId, e);
            OcppCallError error = new OcppCallError(
                    "unknown",
                    OcppErrorCode.INTERNAL_ERROR,
                    "Internal error: " + e.getMessage(),
                    null
            );
            session.sendMessage(new TextMessage(messageParser.serializeCallError(error)));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String stationId = extractStationId(session);
        sessionManager.unregister(stationId);
        log.info("OCPP WebSocket 연결 종료: stationId={}, status={}", stationId, status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        String stationId = extractStationId(session);
        log.error("OCPP WebSocket 전송 오류: stationId={}", stationId, exception);
        sessionManager.unregister(stationId);
    }

    private String extractStationId(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri == null) {
            throw new IllegalStateException("WebSocket URI is null");
        }
        String path = uri.getPath();
        String[] segments = path.split("/");
        return segments[segments.length - 1];
    }
}
