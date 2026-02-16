package com.charging.adapter.in.websocket.handler;

import com.charging.adapter.in.websocket.OcppAction;
import com.charging.adapter.in.websocket.handler.dto.StatusNotificationRequest;
import com.charging.adapter.in.websocket.handler.dto.StatusNotificationResponse;
import com.charging.adapter.in.websocket.support.annotation.OcppPayload;
import com.charging.adapter.in.websocket.support.annotation.StationId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StatusNotificationHandler {

    @OcppAction("StatusNotification")
    public StatusNotificationResponse handle(@OcppPayload StatusNotificationRequest request,
                                             @StationId String stationId) {
        log.info("StatusNotification 수신: stationId={}, evseId={}, connectorId={}, status={}",
                stationId,
                request.evseId(),
                request.connectorId(),
                request.connectorStatus());

        return StatusNotificationResponse.empty();
    }
}
