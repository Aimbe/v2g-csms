package com.charging.adapter.in.websocket.handler;

import com.charging.adapter.in.websocket.OcppAction;
import com.charging.adapter.in.websocket.OcppSession;
import com.charging.adapter.in.websocket.handler.dto.BootNotificationRequest;
import com.charging.adapter.in.websocket.handler.dto.BootNotificationResponse;
import com.charging.adapter.in.websocket.support.annotation.OcppPayload;
import com.charging.domain.port.in.BootNotificationUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BootNotificationHandler {

    private final BootNotificationUseCase bootNotificationUseCase;

    @OcppAction("BootNotification")
    public BootNotificationResponse handle(@OcppPayload BootNotificationRequest request, OcppSession session) {
        log.info("BootNotification 수신: stationId={}, model={}, vendor={}",
                session.getStationId(),
                request.chargingStation().model(),
                request.chargingStation().vendorName());

        var station = bootNotificationUseCase.processBootNotification(
                session.getStationId(),
                request.chargingStation().model(),
                request.chargingStation().vendorName(),
                request.reason()
        );

        if (station.isPresent()) {
            return BootNotificationResponse.accepted(300);
        } else {
            return BootNotificationResponse.rejected("Unknown charging station");
        }
    }
}
