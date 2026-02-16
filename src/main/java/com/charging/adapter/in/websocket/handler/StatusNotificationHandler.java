package com.charging.adapter.in.websocket.handler;

import com.charging.adapter.in.websocket.OcppAction;
import com.charging.adapter.in.websocket.OcppSession;
import com.charging.adapter.in.websocket.handler.dto.StatusNotificationRequest;
import com.charging.adapter.in.websocket.handler.dto.StatusNotificationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StatusNotificationHandler {

    @OcppAction("StatusNotification")
    public StatusNotificationResponse handle(StatusNotificationRequest request, OcppSession session) {
        log.info("StatusNotification 수신: stationId={}, evseId={}, connectorId={}, status={}",
                session.getStationId(),
                request.evseId(),
                request.connectorId(),
                request.connectorStatus());

        return StatusNotificationResponse.empty();
    }
}
