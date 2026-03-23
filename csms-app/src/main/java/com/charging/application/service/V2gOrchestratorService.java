package com.charging.application.service;

import com.charging.application.outbox.V2gScheduleProposalOutboxFactory;
import com.charging.application.outbox.dto.ChargingNeedsEventPayload;
import com.charging.domain.entity.V2gScheduleProposal;
import com.charging.domain.enums.EnergyTransferModeEnum;
import com.charging.domain.enums.V2gProposalActionEnum;
import com.charging.domain.port.out.OutboxEventPort;
import com.charging.domain.port.out.V2gScheduleProposalPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class V2gOrchestratorService {

    private static final BigDecimal DEFAULT_CHARGE_POWER = new BigDecimal("7000");
    private static final BigDecimal DEFAULT_DISCHARGE_POWER = new BigDecimal("5000");
    private static final BigDecimal MAX_DISCHARGE_POWER = new BigDecimal("10000");

    private final V2gScheduleProposalPort v2gScheduleProposalPort;
    private final OutboxEventPort outboxEventPort;
    private final V2gScheduleProposalOutboxFactory v2gScheduleProposalOutboxFactory;

    @Transactional
    public V2gScheduleProposal orchestrate(ChargingNeedsEventPayload payload) {
        boolean bidirectionalRequest = isBidirectional(payload.requestedEnergyTransfer());
        BigDecimal candidatePower = payload.dcMaxPower() != null ? payload.dcMaxPower()
                : (bidirectionalRequest ? DEFAULT_DISCHARGE_POWER : DEFAULT_CHARGE_POWER);

        V2gProposalActionEnum proposalAction = bidirectionalRequest
                ? V2gProposalActionEnum.DISCHARGE
                : V2gProposalActionEnum.CHARGE;

        BigDecimal proposedPower = bidirectionalRequest
                ? candidatePower.min(MAX_DISCHARGE_POWER)
                : candidatePower.min(DEFAULT_CHARGE_POWER);

        LocalDateTime validUntil = payload.departureTime() != null
                ? payload.departureTime()
                : LocalDateTime.now().plusHours(2);

        String reason = bidirectionalRequest
                ? "Bidirectional transfer requested; proposing discharge-capable schedule."
                : "Charge-focused schedule proposed from latest charging needs.";

        V2gScheduleProposal proposal = V2gScheduleProposal.builder()
                .stationId(payload.stationId())
                .evseId(payload.evseId())
                .sourceChargingNeedsId(payload.chargingNeedsId())
                .requestedEnergyTransfer(payload.requestedEnergyTransfer())
                .proposalAction(proposalAction)
                .chargingRateUnit("W")
                .proposedPower(proposedPower)
                .targetSoc(payload.dcTargetSoc())
                .validUntil(validUntil)
                .reason(reason)
                .build();

        V2gScheduleProposal saved = v2gScheduleProposalPort.save(proposal);
        outboxEventPort.save(v2gScheduleProposalOutboxFactory.create(saved));

        log.info("V2G schedule proposal 생성 완료: stationId={}, evseId={}, action={}, power={}",
                saved.getStationId(), saved.getEvseId(), saved.getProposalAction(), saved.getProposedPower());

        return saved;
    }

    private boolean isBidirectional(EnergyTransferModeEnum energyTransferMode) {
        return energyTransferMode == EnergyTransferModeEnum.AC_BPT_SINGLE_PHASE
                || energyTransferMode == EnergyTransferModeEnum.AC_BPT_THREE_PHASE
                || energyTransferMode == EnergyTransferModeEnum.DC_BPT;
    }
}
