package com.charging.domain.port.in;

import com.charging.domain.entity.MeterValue;
import com.charging.domain.enums.MeasurandEnum;
import java.util.List;

public interface MeterValueQueryUseCase {
    List<MeterValue> getMeterValuesByTransactionId(Long transactionId);
    List<MeterValue> getMeterValuesByMeasurand(Long transactionId, MeasurandEnum measurand);
    MeterValue getLatestMeterValue(Long transactionId);
}
