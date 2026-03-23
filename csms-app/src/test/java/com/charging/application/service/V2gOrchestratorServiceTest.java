package com.charging.application.service;

import com.charging.application.outbox.V2gScheduleProposalOutboxFactory;
import com.charging.application.outbox.dto.ChargingNeedsEventPayload;
import com.charging.domain.entity.OutboxEvent;
import com.charging.domain.entity.V2gScheduleProposal;
import com.charging.domain.enums.EnergyTransferModeEnum;
import com.charging.domain.enums.V2gProposalActionEnum;
import com.charging.domain.port.out.OutboxEventPort;
import com.charging.domain.port.out.V2gScheduleProposalPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class V2gOrchestratorServiceTest {

    private V2gScheduleProposalPort v2gScheduleProposalPort;
    private OutboxEventPort outboxEventPort;
    private V2gOrchestratorService v2gOrchestratorService;

    @BeforeEach
    void setUp() {
        v2gScheduleProposalPort = mock(V2gScheduleProposalPort.class);
        outboxEventPort = mock(OutboxEventPort.class);

        V2gScheduleProposalOutboxFactory outboxFactory =
                new V2gScheduleProposalOutboxFactory(new ObjectMapper(), "ocpp.v2g.charging-schedule.v1");

        v2gOrchestratorService = new V2gOrchestratorService(
                v2gScheduleProposalPort,
                outboxEventPort,
                outboxFactory
        );
    }

    @Test
    void orchestrate_shouldCreateDischargeProposalForBidirectionalRequest() {
        ChargingNeedsEventPayload payload = new ChargingNeedsEventPayload(
                11L,
                "ST-001",
                1,
                EnergyTransferModeEnum.DC_BPT,
                LocalDateTime.now().plusHours(3),
                new BigDecimal("12000"),
                85,
                new BigDecimal("75000")
        );

        when(v2gScheduleProposalPort.save(any(V2gScheduleProposal.class)))
                .thenAnswer(invocation -> {
                    V2gScheduleProposal proposal = invocation.getArgument(0);
                    return V2gScheduleProposal.builder()
                            .id(99L)
                            .stationId(proposal.getStationId())
                            .evseId(proposal.getEvseId())
                            .sourceChargingNeedsId(proposal.getSourceChargingNeedsId())
                            .requestedEnergyTransfer(proposal.getRequestedEnergyTransfer())
                            .proposalAction(proposal.getProposalAction())
                            .chargingRateUnit(proposal.getChargingRateUnit())
                            .proposedPower(proposal.getProposedPower())
                            .targetSoc(proposal.getTargetSoc())
                            .validUntil(proposal.getValidUntil())
                            .proposalStatus(proposal.getProposalStatus())
                            .reason(proposal.getReason())
                            .build();
                });
        when(outboxEventPort.save(any(OutboxEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));

        V2gScheduleProposal proposal = v2gOrchestratorService.orchestrate(payload);

        assertThat(proposal.getProposalAction()).isEqualTo(V2gProposalActionEnum.DISCHARGE);
        assertThat(proposal.getProposedPower()).isEqualByComparingTo("10000");
        verify(outboxEventPort).save(any(OutboxEvent.class));
    }
}
