package com.charging.application.service;

import com.charging.domain.port.in.DashboardUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardUseCase {

    private final DashboardProjectionService dashboardProjectionService;

    @Value("${app.dashboard.projection.enabled:true}")
    private boolean dashboardProjectionEnabled;

    @Override
    public DashboardSummary getDashboardSummary() {
        if (dashboardProjectionEnabled) {
            return dashboardProjectionService.getProjectedSummaryOrRefresh();
        }

        return dashboardProjectionService.getLiveSummary();
    }
}
