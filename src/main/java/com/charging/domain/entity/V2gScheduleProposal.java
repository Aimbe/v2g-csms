package com.charging.domain.entity;

import com.charging.domain.enums.EnergyTransferModeEnum;
import com.charging.domain.enums.V2gProposalActionEnum;
import com.charging.domain.enums.V2gProposalStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "V2G_SCHEDULE_PROPOSAL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class V2gScheduleProposal extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "station_id", length = 50, nullable = false)
    private String stationId;

    @Column(name = "evse_id", nullable = false)
    private Integer evseId;

    @Column(name = "source_charging_needs_id", nullable = false)
    private Long sourceChargingNeedsId;

    @Enumerated(EnumType.STRING)
    @Column(name = "requested_energy_transfer", nullable = false)
    private EnergyTransferModeEnum requestedEnergyTransfer;

    @Enumerated(EnumType.STRING)
    @Column(name = "proposal_action", nullable = false)
    private V2gProposalActionEnum proposalAction;

    @Column(name = "charging_rate_unit", length = 10, nullable = false)
    private String chargingRateUnit;

    @Column(name = "proposed_power", precision = 15, scale = 2, nullable = false)
    private BigDecimal proposedPower;

    @Column(name = "target_soc")
    private Integer targetSoc;

    @Column(name = "valid_until")
    private LocalDateTime validUntil;

    @Enumerated(EnumType.STRING)
    @Column(name = "proposal_status", nullable = false)
    @Builder.Default
    private V2gProposalStatusEnum proposalStatus = V2gProposalStatusEnum.PROPOSED;

    @Column(name = "reason", length = 500)
    private String reason;
}
