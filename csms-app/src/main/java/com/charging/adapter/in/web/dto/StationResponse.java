package com.charging.adapter.in.web.dto;

import java.math.BigDecimal;
import java.util.List;

public record StationResponse(
    Long id,
    String stationId,
    BigDecimal powerGridCapacity,
    Boolean v2gSupported,
    Boolean iso15118Supported,
    List<EvseResponse> evses
) {
    public static StationResponse from(com.charging.domain.entity.Station station) {
        List<EvseResponse> evseResponses = station.getEvses() != null
                ? station.getEvses().stream().map(EvseResponse::from).toList()
                : List.of();
        return new StationResponse(
                station.getId(),
                station.getStationId(),
                station.getPowerGridCapacity(),
                station.getV2gSupported(),
                station.getIso15118Supported(),
                evseResponses
        );
    }

    public static StationResponse summary(com.charging.domain.entity.Station station) {
        return new StationResponse(
                station.getId(),
                station.getStationId(),
                station.getPowerGridCapacity(),
                station.getV2gSupported(),
                station.getIso15118Supported(),
                List.of()
        );
    }
}
