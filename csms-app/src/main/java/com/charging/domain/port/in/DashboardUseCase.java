package com.charging.domain.port.in;

public interface DashboardUseCase {
    DashboardSummary getDashboardSummary();

    record DashboardSummary(
        long totalStations,
        long totalEvses,
        long availableConnectors,
        long occupiedConnectors,
        long activeTransactions,
        long todayTransactions
    ) {}
}
