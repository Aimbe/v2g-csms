package com.charging.adapter.in.websocket.handler;

import com.charging.adapter.in.websocket.OcppAction;
import com.charging.adapter.in.websocket.handler.dto.ClearedChargingLimitRequest;
import com.charging.adapter.in.websocket.handler.dto.ClearedChargingLimitResponse;
import com.charging.adapter.in.websocket.support.annotation.OcppPayload;
import com.charging.adapter.in.websocket.support.annotation.StationId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClearedChargingLimitHandler {

    @OcppAction("ClearedChargingLimit")
    public ClearedChargingLimitResponse handle(@OcppPayload ClearedChargingLimitRequest request,
                                                @StationId String stationId) {
        log.info("ClearedChargingLimit 수신: stationId={}, source={}, evseId={}",
                stationId, request.chargingLimitSource(), request.evseId());

        return ClearedChargingLimitResponse.empty();
    }
}
