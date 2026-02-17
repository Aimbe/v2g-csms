package com.charging.adapter.in.web;

import com.charging.adapter.in.web.dto.MeterValueResponse;
import com.charging.domain.entity.MeterValue;
import com.charging.domain.enums.MeasurandEnum;
import com.charging.domain.port.in.MeterValueQueryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/meter-values")
@RequiredArgsConstructor
public class MeterValueController {

    private final MeterValueQueryUseCase meterValueQueryUseCase;

    @GetMapping
    public ResponseEntity<List<MeterValueResponse>> getMeterValues(
            @RequestParam Long transactionId,
            @RequestParam(required = false) String measurand) {
        List<MeterValue> meterValues;
        if (measurand != null && !measurand.isBlank()) {
            meterValues = meterValueQueryUseCase.getMeterValuesByMeasurand(
                    transactionId, MeasurandEnum.valueOf(measurand));
        } else {
            meterValues = meterValueQueryUseCase.getMeterValuesByTransactionId(transactionId);
        }
        List<MeterValueResponse> responses = meterValues.stream()
                .map(MeterValueResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
