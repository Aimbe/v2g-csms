package com.charging.adapter.in.websocket.handler;

import com.charging.adapter.in.websocket.OcppAction;
import com.charging.adapter.in.websocket.handler.dto.NotifyEVChargingScheduleRequest;
import com.charging.adapter.in.websocket.handler.dto.NotifyEVChargingScheduleResponse;
import com.charging.adapter.in.websocket.support.annotation.OcppPayload;
import com.charging.adapter.in.websocket.support.annotation.StationId;
import com.charging.domain.entity.ChargingSchedulePeriod;
import com.charging.domain.port.out.ChargingSchedulePeriodPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotifyEVChargingScheduleHandler {

    private final ChargingSchedulePeriodPort chargingSchedulePeriodPort;

    @Transactional
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

        chargingSchedulePeriodPort.deleteByStationIdAndEvseId(stationId, request.evseId());

        List<ChargingSchedulePeriod> periods = new ArrayList<>();
        if (request.chargingSchedule().chargingSchedulePeriod() != null) {
            for (NotifyEVChargingScheduleRequest.ChargingSchedulePeriodInfo periodInfo :
                    request.chargingSchedule().chargingSchedulePeriod()) {
                periods.add(ChargingSchedulePeriod.builder()
                        .stationId(stationId)
                        .evseId(request.evseId())
                        .chargingScheduleId(request.chargingSchedule().id())
                        .chargingRateUnit(request.chargingSchedule().chargingRateUnit())
                        .startPeriod(periodInfo.startPeriod())
                        .limitValue(BigDecimal.valueOf(periodInfo.limit()))
                        .numberPhases(periodInfo.numberPhases())
                        .build());
            }
        }

        chargingSchedulePeriodPort.saveAll(periods);
        log.info("ChargingSchedulePeriod 저장 완료: stationId={}, evseId={}, count={}",
                stationId, request.evseId(), periods.size());

        return NotifyEVChargingScheduleResponse.accepted();
    }
}
