package com.charging.adapter.in.web;

import com.charging.adapter.in.web.dto.StationResponse;
import com.charging.domain.entity.Station;
import com.charging.domain.port.in.StationQueryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/stations")
@RequiredArgsConstructor
public class StationController {

    private final StationQueryUseCase stationQueryUseCase;

    @GetMapping
    public ResponseEntity<List<StationResponse>> getAllStations() {
        List<Station> stations = stationQueryUseCase.getAllStations();
        List<StationResponse> responses = stations.stream()
                .map(StationResponse::summary)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{stationId}")
    public ResponseEntity<StationResponse> getStationDetail(@PathVariable String stationId) {
        Station station = stationQueryUseCase.getStationDetail(stationId);
        return ResponseEntity.ok(StationResponse.from(station));
    }
}
