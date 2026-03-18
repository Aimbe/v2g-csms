package com.charging.adapter.out.persistence.adapter;

import com.charging.adapter.out.persistence.repository.V2gScheduleProposalRepository;
import com.charging.domain.entity.V2gScheduleProposal;
import com.charging.domain.port.out.V2gScheduleProposalPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class V2gScheduleProposalPersistenceAdapter implements V2gScheduleProposalPort {

    private final V2gScheduleProposalRepository v2gScheduleProposalRepository;

    @Override
    public V2gScheduleProposal save(V2gScheduleProposal proposal) {
        return v2gScheduleProposalRepository.save(proposal);
    }

    @Override
    public List<V2gScheduleProposal> findByStationIdAndEvseId(String stationId, Integer evseId) {
        return v2gScheduleProposalRepository.findByStationIdAndEvseId(stationId, evseId);
    }
}
