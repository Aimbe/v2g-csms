package com.charging.adapter.in.websocket.handler;

import com.charging.adapter.in.websocket.OcppAction;
import com.charging.adapter.in.websocket.handler.dto.HeartbeatRequest;
import com.charging.adapter.in.websocket.handler.dto.HeartbeatResponse;
import com.charging.adapter.in.websocket.support.annotation.MessageId;
import com.charging.adapter.in.websocket.support.annotation.OcppPayload;
import com.charging.adapter.in.websocket.support.annotation.StationId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HeartbeatHandler {

    @OcppAction("Heartbeat")
    public HeartbeatResponse handle(@OcppPayload HeartbeatRequest request,
                                    @StationId String stationId,
                                    @MessageId String messageId) {
        log.debug("Heartbeat 수신: stationId={}, messageId={}", stationId, messageId);
        return HeartbeatResponse.now();
    }
}
