package com.charging.adapter.in.web;

import com.charging.adapter.in.web.dto.ChargingNeedsResponse;
import com.charging.domain.port.in.ChargingNeedsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/charging-needs")
@RequiredArgsConstructor
public class ChargingNeedsController {

    private final ChargingNeedsUseCase chargingNeedsUseCase;

    @GetMapping("/latest")
    public ResponseEntity<ChargingNeedsResponse> getLatestChargingNeeds(
            @RequestParam String stationId,
            @RequestParam Integer evseId) {
        return chargingNeedsUseCase.getLatestChargingNeeds(stationId, evseId)
                .map(cn -> ResponseEntity.ok(ChargingNeedsResponse.from(cn)))
                .orElse(ResponseEntity.notFound().build());
    }
}
