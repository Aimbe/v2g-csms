package com.charging.domain.port.out;

import com.charging.domain.entity.MeterValue;
import com.charging.domain.enums.MeasurandEnum;
import java.util.List;

public interface MeterValuePort {
    MeterValue save(MeterValue meterValue);
    List<MeterValue> findByTransactionIdFkOrderByTimestampAsc(Long transactionId);
    List<MeterValue> findByTransactionIdFkAndMeasurand(Long transactionId, MeasurandEnum measurand);
    MeterValue findLatestByTransactionId(Long transactionId);
}
