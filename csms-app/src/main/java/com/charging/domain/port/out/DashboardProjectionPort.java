package com.charging.domain.port.out;

import com.charging.domain.entity.DashboardProjection;

import java.util.Optional;

public interface DashboardProjectionPort {
    Optional<DashboardProjection> findGlobal();
    DashboardProjection save(DashboardProjection dashboardProjection);
}
