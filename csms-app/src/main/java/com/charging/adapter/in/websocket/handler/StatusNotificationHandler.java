package com.charging.adapter.in.websocket.handler;

import com.charging.adapter.in.websocket.OcppAction;
import com.charging.adapter.in.websocket.handler.dto.StatusNotificationRequest;
import com.charging.adapter.in.websocket.handler.dto.StatusNotificationResponse;
import com.charging.adapter.in.websocket.support.annotation.OcppPayload;
import com.charging.adapter.in.websocket.support.annotation.StationId;
import com.charging.domain.enums.ConnectorStatusEnum;
import com.charging.domain.port.out.ConnectorPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatusNotificationHandler {

    private final ConnectorPort connectorPort;

    @OcppAction("StatusNotification")
    public StatusNotificationResponse handle(@OcppPayload StatusNotificationRequest request,
                                             @StationId String stationId) {
        log.info("StatusNotification 수신: stationId={}, evseId={}, connectorId={}, status={}",
                stationId,
                request.evseId(),
                request.connectorId(),
                request.connectorStatus());

        connectorPort.findByEvseIdAndStationIdAndConnectorId(request.evseId(), stationId, request.connectorId())
                .ifPresentOrElse(
                        connector -> {
                            connector.updateStatus(ConnectorStatusEnum.valueOf(request.connectorStatus()));
                            connectorPort.save(connector);
                        },
                        () -> log.warn("커넥터를 찾을 수 없습니다: stationId={}, evseId={}, connectorId={}",
                                stationId, request.evseId(), request.connectorId())
                );

        return StatusNotificationResponse.empty();
    }
}
