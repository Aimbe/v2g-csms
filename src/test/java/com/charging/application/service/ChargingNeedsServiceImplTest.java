package com.charging.application.service;

import com.charging.application.outbox.ChargingNeedsOutboxFactory;
import com.charging.domain.entity.ChargingNeeds;
import com.charging.domain.entity.OutboxEvent;
import com.charging.domain.enums.EnergyTransferModeEnum;
import com.charging.domain.port.out.ChargingNeedsPort;
import com.charging.domain.port.out.OutboxEventPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ChargingNeedsServiceImplTest {

    private ChargingNeedsPort chargingNeedsPort;
    private OutboxEventPort outboxEventPort;
    private ChargingNeedsServiceImpl chargingNeedsService;

    @BeforeEach
    void setUp() {
        chargingNeedsPort = mock(ChargingNeedsPort.class);
        outboxEventPort = mock(OutboxEventPort.class);

        ChargingNeedsOutboxFactory outboxFactory =
                new ChargingNeedsOutboxFactory(new ObjectMapper(), "ocpp.v2g.charging-needs.v1");

        chargingNeedsService = new ChargingNeedsServiceImpl(
                chargingNeedsPort,
                outboxEventPort,
                outboxFactory
        );
    }

    @Test
    void saveChargingNeeds_shouldAppendOutboxEvent() {
        ChargingNeeds savedChargingNeeds = ChargingNeeds.builder()
                .id(10L)
                .stationId("ST-001")
                .evseId(1)
                .requestedEnergyTransfer(EnergyTransferModeEnum.DC_BPT)
                .dcTargetSoc(80)
                .dcMaxPower(new BigDecimal("9000"))
                .build();

        when(chargingNeedsPort.save(any(ChargingNeeds.class))).thenReturn(savedChargingNeeds);
        when(outboxEventPort.save(any(OutboxEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ChargingNeeds result = chargingNeedsService.saveChargingNeeds(
                "ST-001",
                1,
                EnergyTransferModeEnum.DC_BPT,
                null,
                80,
                100000,
                200,
                400,
                9000
        );

        verify(outboxEventPort).save(any(OutboxEvent.class));
        assertThat(result.getId()).isEqualTo(10L);
    }

    @Test
    void getLatestChargingNeeds_shouldDelegateToPort() {
        when(chargingNeedsPort.findLatestByStationIdAndEvseId("ST-001", 1)).thenReturn(Optional.empty());

        assertThat(chargingNeedsService.getLatestChargingNeeds("ST-001", 1)).isEmpty();
        verify(chargingNeedsPort).findLatestByStationIdAndEvseId("ST-001", 1);
    }
}
