package com.charging.application.service;

import com.charging.domain.entity.DashboardProjection;
import com.charging.domain.enums.ConnectorStatusEnum;
import com.charging.domain.port.in.DashboardUseCase;
import com.charging.domain.port.out.ConnectorPort;
import com.charging.domain.port.out.DashboardProjectionPort;
import com.charging.domain.port.out.EvsePort;
import com.charging.domain.port.out.StationPort;
import com.charging.domain.port.out.TransactionPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardProjectionService {

    private final StationPort stationPort;
    private final EvsePort evsePort;
    private final ConnectorPort connectorPort;
    private final TransactionPort transactionPort;
    private final DashboardProjectionPort dashboardProjectionPort;

    public DashboardUseCase.DashboardSummary getProjectedSummaryOrRefresh() {
        return dashboardProjectionPort.findGlobal()
                .map(DashboardProjection::toSummary)
                .orElseGet(this::refreshProjection);
    }

    public DashboardUseCase.DashboardSummary getLiveSummary() {
        return calculateCurrentSummary();
    }

    @Transactional
    public DashboardUseCase.DashboardSummary refreshProjection() {
        DashboardUseCase.DashboardSummary summary = calculateCurrentSummary();

        DashboardProjection projection = dashboardProjectionPort.findGlobal()
                .orElseGet(() -> DashboardProjection.builder()
                        .projectionKey(DashboardProjection.GLOBAL_KEY)
                        .build());

        projection.update(summary);
        dashboardProjectionPort.save(projection);
        log.debug("Dashboard projection 갱신 완료");

        return summary;
    }

    private DashboardUseCase.DashboardSummary calculateCurrentSummary() {
        long totalStations = stationPort.findAll().size();
        long totalEvses = evsePort.count();
        long availableConnectors = connectorPort.countByStatus(ConnectorStatusEnum.AVAILABLE);
        long occupiedConnectors = connectorPort.countByStatus(ConnectorStatusEnum.OCCUPIED);
        long activeTransactions = transactionPort.countActiveTransactions();

        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        long todayTransactions = transactionPort.countByStartTimeBetween(todayStart, todayEnd);

        return new DashboardUseCase.DashboardSummary(
                totalStations,
                totalEvses,
                availableConnectors,
                occupiedConnectors,
                activeTransactions,
                todayTransactions
        );
    }
}
