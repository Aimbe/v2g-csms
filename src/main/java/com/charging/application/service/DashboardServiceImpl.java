package com.charging.application.service;

import com.charging.domain.enums.ConnectorStatusEnum;
import com.charging.domain.port.in.DashboardUseCase;
import com.charging.domain.port.out.ConnectorPort;
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
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardUseCase {

    private final StationPort stationPort;
    private final EvsePort evsePort;
    private final ConnectorPort connectorPort;
    private final TransactionPort transactionPort;

    @Override
    public DashboardSummary getDashboardSummary() {
        long totalStations = stationPort.findAll().size();
        long totalEvses = evsePort.count();
        long availableConnectors = connectorPort.countByStatus(ConnectorStatusEnum.AVAILABLE);
        long occupiedConnectors = connectorPort.countByStatus(ConnectorStatusEnum.OCCUPIED);
        long activeTransactions = transactionPort.countActiveTransactions();

        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        long todayTransactions = transactionPort.countByStartTimeBetween(todayStart, todayEnd);

        return new DashboardSummary(
                totalStations, totalEvses, availableConnectors,
                occupiedConnectors, activeTransactions, todayTransactions
        );
    }
}
