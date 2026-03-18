package com.charging.adapter.in.websocket.handler;

import com.charging.adapter.in.websocket.handler.dto.MeterValuesRequest;
import com.charging.application.outbox.MeterValuesOutboxFactory;
import com.charging.domain.entity.MeterValue;
import com.charging.domain.entity.OutboxEvent;
import com.charging.domain.entity.Transaction;
import com.charging.domain.enums.ChargingStateEnum;
import com.charging.domain.enums.TransactionEventEnum;
import com.charging.domain.port.out.MeterValuePort;
import com.charging.domain.port.out.OutboxEventPort;
import com.charging.domain.port.out.TransactionPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MeterValuesHandlerTest {

    private MeterValuePort meterValuePort;
    private TransactionPort transactionPort;
    private OutboxEventPort outboxEventPort;
    private MeterValuesHandler meterValuesHandler;

    @BeforeEach
    void setUp() {
        meterValuePort = mock(MeterValuePort.class);
        transactionPort = mock(TransactionPort.class);
        outboxEventPort = mock(OutboxEventPort.class);

        MeterValuesOutboxFactory meterValuesOutboxFactory =
                new MeterValuesOutboxFactory(new ObjectMapper(), "ocpp.meter-value.v1");

        meterValuesHandler = new MeterValuesHandler(
                meterValuePort,
                transactionPort,
                outboxEventPort,
                meterValuesOutboxFactory
        );
    }

    @Test
    void handle_shouldBatchSaveMeterValuesAndAppendOutboxEvent() {
        Transaction transaction = Transaction.builder()
                .transactionId("TXN-123")
                .evseId(1)
                .stationId("ST-001")
                .connectorId(1)
                .idToken("ID-TOKEN")
                .eventType(TransactionEventEnum.STARTED)
                .chargingState(ChargingStateEnum.CHARGING)
                .startTime(LocalDateTime.now())
                .build();

        MeterValuesRequest request = new MeterValuesRequest(
                1,
                List.of(
                        new MeterValuesRequest.MeterValueData(
                                "2026-03-17T12:00:00",
                                List.of(
                                        new MeterValuesRequest.SampledValueData(
                                                32.5,
                                                "Power.Active.Import",
                                                "W",
                                                "Sample.Periodic",
                                                "Outlet"
                                        ),
                                        new MeterValuesRequest.SampledValueData(
                                                80,
                                                "SoC",
                                                "%",
                                                "Sample.Periodic",
                                                "Body"
                                        )
                                )
                        )
                )
        );

        when(transactionPort.findActiveTransactionByStationIdAndEvseId("ST-001", 1))
                .thenReturn(Optional.of(transaction));
        when(meterValuePort.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(outboxEventPort.save(any(OutboxEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));

        meterValuesHandler.handle(request, "ST-001");

        ArgumentCaptor<List<MeterValue>> meterValuesCaptor = ArgumentCaptor.forClass(List.class);
        verify(meterValuePort).saveAll(meterValuesCaptor.capture());
        verify(outboxEventPort).save(any(OutboxEvent.class));

        List<MeterValue> savedMeterValues = meterValuesCaptor.getValue();
        assertThat(savedMeterValues).hasSize(2);

        ArgumentCaptor<OutboxEvent> outboxCaptor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxEventPort).save(outboxCaptor.capture());

        OutboxEvent outboxEvent = outboxCaptor.getValue();
        assertThat(outboxEvent.getEventType()).isEqualTo("MeterValuesBatchRecorded");
        assertThat(outboxEvent.getTopic()).isEqualTo("ocpp.meter-value.v1");
        assertThat(outboxEvent.getPartitionKey()).isEqualTo("ST-001:1");
        assertThat(outboxEvent.getPayload()).contains("TXN-123");
    }
}
