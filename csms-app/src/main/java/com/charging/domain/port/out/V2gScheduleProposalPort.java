package com.charging.domain.port.out;

import com.charging.domain.entity.V2gScheduleProposal;

import java.util.List;

public interface V2gScheduleProposalPort {
    V2gScheduleProposal save(V2gScheduleProposal proposal);
    List<V2gScheduleProposal> findByStationIdAndEvseId(String stationId, Integer evseId);
}
