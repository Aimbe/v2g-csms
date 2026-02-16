package com.charging.adapter.out.persistence.adapter;

import com.charging.adapter.out.persistence.repository.MeterValueRepository;
import com.charging.domain.entity.MeterValue;
import com.charging.domain.enums.MeasurandEnum;
import com.charging.domain.port.out.MeterValuePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MeterValuePersistenceAdapter implements MeterValuePort {

    private final MeterValueRepository meterValueRepository;

    @Override
    public MeterValue save(MeterValue meterValue) {
        return meterValueRepository.save(meterValue);
    }

    @Override
    public List<MeterValue> findByTransactionIdFkOrderByTimestampAsc(Long transactionId) {
        return meterValueRepository.findByTransactionIdFkOrderByTimestampAsc(transactionId);
    }

    @Override
    public List<MeterValue> findByTransactionIdFkAndMeasurand(Long transactionId, MeasurandEnum measurand) {
        return meterValueRepository.findByTransactionIdFkAndMeasurand(transactionId, measurand);
    }

    @Override
    public MeterValue findLatestByTransactionId(Long transactionId) {
        return meterValueRepository.findLatestByTransactionId(transactionId);
    }
}
