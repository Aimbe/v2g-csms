package com.charging.adapter.in.websocket.handler;

import com.charging.adapter.in.websocket.OcppAction;
import com.charging.adapter.in.websocket.handler.dto.ClearedChargingLimitRequest;
import com.charging.adapter.in.websocket.handler.dto.ClearedChargingLimitResponse;
import com.charging.adapter.in.websocket.support.annotation.OcppPayload;
import com.charging.adapter.in.websocket.support.annotation.StationId;
import com.charging.domain.entity.ChargingProfile;
import com.charging.domain.port.out.ChargingProfilePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClearedChargingLimitHandler {

    private final ChargingProfilePort chargingProfilePort;

    @OcppAction("ClearedChargingLimit")
    public ClearedChargingLimitResponse handle(@OcppPayload ClearedChargingLimitRequest request,
                                                @StationId String stationId) {
        log.info("ClearedChargingLimit 수신: stationId={}, source={}, evseId={}",
                stationId, request.chargingLimitSource(), request.evseId());

        List<ChargingProfile> activeProfiles = chargingProfilePort.findActiveProfilesByStationId(stationId, LocalDateTime.now());

        List<ChargingProfile> profilesToDeactivate = activeProfiles.stream()
                .filter(profile -> request.evseId() == null || request.evseId().equals(profile.getEvseId()))
                .toList();

        profilesToDeactivate.forEach(profile -> {
            profile.deactivate();
            chargingProfilePort.save(profile);
        });

        log.info("ClearedChargingLimit 처리 완료: stationId={}, 비활성화된 프로파일 수={}", stationId, profilesToDeactivate.size());

        return ClearedChargingLimitResponse.empty();
    }
}
