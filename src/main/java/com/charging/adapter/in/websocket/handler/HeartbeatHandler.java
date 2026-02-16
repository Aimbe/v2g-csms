package com.charging.adapter.in.websocket.handler;

import com.charging.adapter.in.websocket.OcppAction;
import com.charging.adapter.in.websocket.OcppSession;
import com.charging.adapter.in.websocket.handler.dto.HeartbeatRequest;
import com.charging.adapter.in.websocket.handler.dto.HeartbeatResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HeartbeatHandler {

    @OcppAction("Heartbeat")
    public HeartbeatResponse handle(HeartbeatRequest request, OcppSession session) {
        log.debug("Heartbeat 수신: stationId={}", session.getStationId());
        return HeartbeatResponse.now();
    }
}
