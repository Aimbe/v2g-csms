package com.charging.adapter.in.websocket.handler;

import com.charging.adapter.in.websocket.OcppAction;
import com.charging.adapter.in.websocket.handler.dto.MeterValuesRequest;
import com.charging.adapter.in.websocket.handler.dto.MeterValuesResponse;
import com.charging.adapter.in.websocket.support.annotation.OcppPayload;
import com.charging.adapter.in.websocket.support.annotation.StationId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MeterValuesHandler {

    @OcppAction("MeterValues")
    public MeterValuesResponse handle(@OcppPayload MeterValuesRequest request,
                                      @StationId String stationId) {
        log.info("MeterValues 수신: stationId={}, evseId={}, values={}",
                stationId,
                request.evseId(),
                request.meterValue() != null ? request.meterValue().size() : 0);

        if (request.meterValue() != null) {
            request.meterValue().forEach(mv -> {
                if (mv.sampledValue() != null) {
                    mv.sampledValue().forEach(sv ->
                        log.debug("  측정값: measurand={}, value={}", sv.measurand(), sv.value())
                    );
                }
            });
        }

        return MeterValuesResponse.empty();
    }
}
