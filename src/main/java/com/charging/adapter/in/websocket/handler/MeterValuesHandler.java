package com.charging.adapter.in.websocket.handler;

import com.charging.adapter.in.websocket.OcppAction;
import com.charging.adapter.in.websocket.handler.dto.MeterValuesRequest;
import com.charging.adapter.in.websocket.handler.dto.MeterValuesResponse;
import com.charging.adapter.in.websocket.support.annotation.OcppPayload;
import com.charging.adapter.in.websocket.support.annotation.StationId;
import com.charging.domain.entity.MeterValue;
import com.charging.domain.entity.Transaction;
import com.charging.domain.enums.MeasurandEnum;
import com.charging.domain.port.out.MeterValuePort;
import com.charging.domain.port.out.TransactionPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MeterValuesHandler {

    private final MeterValuePort meterValuePort;
    private final TransactionPort transactionPort;

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

        List<Transaction> activeTransactions = transactionPort.findActiveTransactions(stationId);
        Transaction transaction = activeTransactions.stream()
                .filter(t -> t.getEvseId() == request.evseId())
                .findFirst()
                .orElse(null);

        if (transaction == null) {
            log.warn("MeterValues에 해당하는 활성 트랜잭션 없음: stationId={}, evseId={}",
                    stationId, request.evseId());
            return MeterValuesResponse.empty();
        }

        if (request.meterValue() != null) {
            for (MeterValuesRequest.MeterValueData mv : request.meterValue()) {
                if (mv.sampledValue() == null) continue;
                for (MeterValuesRequest.SampledValueData sv : mv.sampledValue()) {
                    MeasurandEnum measurandEnum;
                    try {
                        String enumName = sv.measurand()
                                .replace(".", "_")
                                .toUpperCase();
                        measurandEnum = MeasurandEnum.valueOf(enumName);
                    } catch (IllegalArgumentException e) {
                        log.debug("알 수 없는 measurand, 건너뜀: {}", sv.measurand());
                        continue;
                    }

                    MeterValue meterValue = MeterValue.builder()
                            .timestamp(LocalDateTime.parse(mv.timestamp()))
                            .measurand(measurandEnum)
                            .value(BigDecimal.valueOf(sv.value()))
                            .unit(sv.unitOfMeasure())
                            .location(sv.location())
                            .build();

                    transaction.addMeterValue(meterValue);
                    meterValuePort.save(meterValue);
                }
            }
        }

        return MeterValuesResponse.empty();
    }
}
