package com.charging.application.service;

import com.charging.application.outbox.TransactionEventOutboxFactory;
import com.charging.domain.entity.Evse;
import com.charging.domain.entity.OutboxEvent;
import com.charging.domain.entity.Transaction;
import com.charging.domain.enums.ChargingStateEnum;
import com.charging.domain.enums.OperationalStatusEnum;
import com.charging.domain.enums.TransactionEventEnum;
import com.charging.domain.port.out.EvsePort;
import com.charging.domain.port.out.OutboxEventPort;
import com.charging.domain.port.out.TransactionPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionServiceImplTest {

    private TransactionPort transactionPort;
    private EvsePort evsePort;
    private OutboxEventPort outboxEventPort;
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        transactionPort = mock(TransactionPort.class);
        evsePort = mock(EvsePort.class);
        outboxEventPort = mock(OutboxEventPort.class);

        TransactionEventOutboxFactory outboxFactory =
                new TransactionEventOutboxFactory(new ObjectMapper(), "ocpp.transaction.v1");

        transactionService = new TransactionServiceImpl(
                transactionPort,
                evsePort,
                outboxEventPort,
                outboxFactory
        );
    }

    @Test
    void startTransaction_shouldAppendOutboxEvent() {
        Evse evse = Evse.builder()
                .evseId(1)
                .stationId("ST-001")
                .maxPower(new BigDecimal("50"))
                .operationalStatus(OperationalStatusEnum.OPERATIVE)
                .build();

        when(evsePort.findByEvseIdAndStationId(1, "ST-001")).thenReturn(Optional.of(evse));
        when(transactionPort.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(outboxEventPort.save(any(OutboxEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction saved = transactionService.startTransaction(1, "ST-001", 1, "ID-TOKEN");

        ArgumentCaptor<OutboxEvent> outboxCaptor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxEventPort).save(outboxCaptor.capture());

        OutboxEvent outboxEvent = outboxCaptor.getValue();

        assertThat(saved.getTransactionId()).startsWith("TXN-");
        assertThat(outboxEvent.getAggregateType()).isEqualTo("Transaction");
        assertThat(outboxEvent.getAggregateId()).isEqualTo(saved.getTransactionId());
        assertThat(outboxEvent.getEventType()).isEqualTo("TransactionStarted");
        assertThat(outboxEvent.getTopic()).isEqualTo("ocpp.transaction.v1");
        assertThat(outboxEvent.getPayload()).contains(saved.getTransactionId());
    }

    @Test
    void updateChargingState_shouldAppendUpdatedOutboxEvent() {
        Transaction transaction = Transaction.builder()
                .transactionId("TXN-123")
                .evseId(1)
                .stationId("ST-001")
                .connectorId(1)
                .idToken("ID-TOKEN")
                .eventType(TransactionEventEnum.STARTED)
                .chargingState(ChargingStateEnum.IDLE)
                .startTime(LocalDateTime.now())
                .build();

        when(transactionPort.findByTransactionId("TXN-123")).thenReturn(Optional.of(transaction));
        when(transactionPort.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(outboxEventPort.save(any(OutboxEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction saved = transactionService.updateChargingState("TXN-123", ChargingStateEnum.CHARGING);

        ArgumentCaptor<OutboxEvent> outboxCaptor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxEventPort).save(outboxCaptor.capture());

        assertThat(saved.getChargingState()).isEqualTo(ChargingStateEnum.CHARGING);
        assertThat(outboxCaptor.getValue().getEventType()).isEqualTo("TransactionStateUpdated");
    }

    @Test
    void stopTransaction_shouldAppendEndedOutboxEvent() {
        Transaction transaction = Transaction.builder()
                .transactionId("TXN-456")
                .evseId(1)
                .stationId("ST-001")
                .connectorId(1)
                .idToken("ID-TOKEN")
                .eventType(TransactionEventEnum.STARTED)
                .chargingState(ChargingStateEnum.CHARGING)
                .startTime(LocalDateTime.now().minusHours(1))
                .startMeterValue(new BigDecimal("1000"))
                .stopMeterValue(new BigDecimal("2500"))
                .build();

        when(transactionPort.findByTransactionId("TXN-456")).thenReturn(Optional.of(transaction));
        when(transactionPort.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(outboxEventPort.save(any(OutboxEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction saved = transactionService.stopTransaction("TXN-456", "Normal");

        ArgumentCaptor<OutboxEvent> outboxCaptor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxEventPort).save(outboxCaptor.capture());

        assertThat(saved.getEventType()).isEqualTo(TransactionEventEnum.ENDED);
        assertThat(saved.getTotalEnergy()).isEqualByComparingTo("1.500");
        assertThat(outboxCaptor.getValue().getEventType()).isEqualTo("TransactionEnded");
    }
}
