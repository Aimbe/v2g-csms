package com.charging.adapter.in.websocket;

import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

@Getter
public class OcppSession {

    private final String stationId;
    private final WebSocketSession webSocketSession;

    public OcppSession(String stationId, WebSocketSession webSocketSession) {
        this.stationId = stationId;
        this.webSocketSession = webSocketSession;
    }

    public String getSessionId() {
        return webSocketSession.getId();
    }

    public boolean isOpen() {
        return webSocketSession.isOpen();
    }
}
