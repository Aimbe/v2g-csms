package com.charging.adapter.out.persistence.repository;

import com.charging.domain.entity.V2gScheduleProposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface V2gScheduleProposalRepository extends JpaRepository<V2gScheduleProposal, Long> {
    List<V2gScheduleProposal> findByStationIdAndEvseId(String stationId, Integer evseId);
}
