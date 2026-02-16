package com.charging.adapter.in.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class OcppSessionManager {

    private final ConcurrentHashMap<String, OcppSession> sessions = new ConcurrentHashMap<>();

    public OcppSession register(String stationId, WebSocketSession webSocketSession) {
        OcppSession session = new OcppSession(stationId, webSocketSession);
        sessions.put(stationId, session);
        log.info("충전소 세션 등록: stationId={}, sessionId={}", stationId, webSocketSession.getId());
        return session;
    }

    public void unregister(String stationId) {
        OcppSession removed = sessions.remove(stationId);
        if (removed != null) {
            log.info("충전소 세션 해제: stationId={}", stationId);
        }
    }

    public Optional<OcppSession> getSession(String stationId) {
        return Optional.ofNullable(sessions.get(stationId));
    }

    public int getActiveSessionCount() {
        return sessions.size();
    }
}
