package com.charging.application.service;

import com.charging.domain.entity.MeterValue;
import com.charging.domain.enums.MeasurandEnum;
import com.charging.domain.port.in.MeterValueQueryUseCase;
import com.charging.domain.port.out.MeterValuePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeterValueQueryServiceImpl implements MeterValueQueryUseCase {

    private final MeterValuePort meterValuePort;

    @Override
    public List<MeterValue> getMeterValuesByTransactionId(Long transactionId) {
        return meterValuePort.findByTransactionIdFkOrderByTimestampAsc(transactionId);
    }

    @Override
    public List<MeterValue> getMeterValuesByMeasurand(Long transactionId, MeasurandEnum measurand) {
        return meterValuePort.findByTransactionIdFkAndMeasurand(transactionId, measurand);
    }

    @Override
    public MeterValue getLatestMeterValue(Long transactionId) {
        return meterValuePort.findLatestByTransactionId(transactionId);
    }
}
