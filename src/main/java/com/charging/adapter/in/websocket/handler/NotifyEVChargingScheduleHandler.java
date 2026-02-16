package com.charging.adapter.in.websocket.handler;

import com.charging.adapter.in.websocket.OcppAction;
import com.charging.adapter.in.websocket.handler.dto.NotifyEVChargingScheduleRequest;
import com.charging.adapter.in.websocket.handler.dto.NotifyEVChargingScheduleResponse;
import com.charging.adapter.in.websocket.support.annotation.OcppPayload;
import com.charging.adapter.in.websocket.support.annotation.StationId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotifyEVChargingScheduleHandler {

    @OcppAction("NotifyEVChargingSchedule")
    public NotifyEVChargingScheduleResponse handle(@OcppPayload NotifyEVChargingScheduleRequest request,
                                                    @StationId String stationId) {
        log.info("NotifyEVChargingSchedule 수신: stationId={}, evseId={}, rateUnit={}",
                stationId, request.evseId(),
                request.chargingSchedule().chargingRateUnit());

        if (request.chargingSchedule().chargingSchedulePeriod() != null) {
            request.chargingSchedule().chargingSchedulePeriod().forEach(period ->
                    log.info("  스케줄 기간: startPeriod={}, limit={}, phases={}",
                            period.startPeriod(), period.limit(), period.numberPhases()));
        }

        return NotifyEVChargingScheduleResponse.accepted();
    }
}
