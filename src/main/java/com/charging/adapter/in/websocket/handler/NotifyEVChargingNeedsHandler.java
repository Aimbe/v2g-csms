package com.charging.adapter.in.websocket.handler;

import com.charging.adapter.in.websocket.OcppAction;
import com.charging.adapter.in.websocket.handler.dto.NotifyEVChargingNeedsRequest;
import com.charging.adapter.in.websocket.handler.dto.NotifyEVChargingNeedsResponse;
import com.charging.adapter.in.websocket.support.annotation.OcppPayload;
import com.charging.adapter.in.websocket.support.annotation.StationId;
import com.charging.domain.enums.EnergyTransferModeEnum;
import com.charging.domain.port.in.ChargingNeedsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotifyEVChargingNeedsHandler {

    private final ChargingNeedsUseCase chargingNeedsUseCase;

    @OcppAction("NotifyEVChargingNeeds")
    public NotifyEVChargingNeedsResponse handle(@OcppPayload NotifyEVChargingNeedsRequest request,
                                                 @StationId String stationId) {
        log.info("NotifyEVChargingNeeds 수신: stationId={}, evseId={}, mode={}",
                stationId, request.evseId(),
                request.chargingNeeds().requestedEnergyTransfer());

        try {
            EnergyTransferModeEnum transferMode = EnergyTransferModeEnum.valueOf(
                    request.chargingNeeds().requestedEnergyTransfer());

            LocalDateTime departureTime = request.chargingNeeds().departureTime() != null
                    ? LocalDateTime.parse(request.chargingNeeds().departureTime().replace("Z", ""))
                    : null;

            Integer targetSoc = null;
            Integer evEnergyCapacity = null;
            Integer evMaxCurrent = null;
            Integer evMaxVoltage = null;
            Integer evMaxPower = null;

            if (request.chargingNeeds().dcChargingParameters() != null) {
                var dc = request.chargingNeeds().dcChargingParameters();
                targetSoc = dc.stateOfCharge();
                evEnergyCapacity = dc.evEnergyCapacity();
                evMaxCurrent = dc.evMaxCurrent();
                evMaxVoltage = dc.evMaxVoltage();
                evMaxPower = dc.evMaxPower();
            }

            chargingNeedsUseCase.saveChargingNeeds(
                    stationId, (Integer) request.evseId(), transferMode, departureTime,
                    targetSoc, evEnergyCapacity, evMaxCurrent, evMaxVoltage, evMaxPower);

            return NotifyEVChargingNeedsResponse.accepted();
        } catch (Exception e) {
            log.error("NotifyEVChargingNeeds 처리 실패: stationId={}, error={}", stationId, e.getMessage());
            return NotifyEVChargingNeedsResponse.rejected();
        }
    }
}
