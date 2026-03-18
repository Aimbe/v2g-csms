package com.charging.adapter.out.persistence.adapter;

import com.charging.adapter.out.persistence.repository.DashboardProjectionRepository;
import com.charging.domain.entity.DashboardProjection;
import com.charging.domain.port.out.DashboardProjectionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DashboardProjectionPersistenceAdapter implements DashboardProjectionPort {

    private final DashboardProjectionRepository dashboardProjectionRepository;

    @Override
    public Optional<DashboardProjection> findGlobal() {
        return dashboardProjectionRepository.findById(DashboardProjection.GLOBAL_KEY);
    }

    @Override
    public DashboardProjection save(DashboardProjection dashboardProjection) {
        return dashboardProjectionRepository.save(dashboardProjection);
    }
}
