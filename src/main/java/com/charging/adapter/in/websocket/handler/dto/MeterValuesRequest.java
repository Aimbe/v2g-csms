package com.charging.adapter.in.websocket.handler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MeterValuesRequest(
    int evseId,
    List<MeterValueData> meterValue
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MeterValueData(
        String timestamp,
        List<SampledValueData> sampledValue
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SampledValueData(
        double value,
        String measurand,
        String unitOfMeasure,
        String context,
        String location
    ) {}
}
