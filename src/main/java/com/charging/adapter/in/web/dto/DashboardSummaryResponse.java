package com.charging.adapter.in.web.dto;

public record DashboardSummaryResponse(
    long totalStations,
    long totalEvses,
    long availableConnectors,
    long occupiedConnectors,
    long activeTransactions,
    long todayTransactions
) {
    public static DashboardSummaryResponse from(com.charging.domain.port.in.DashboardUseCase.DashboardSummary summary) {
        return new DashboardSummaryResponse(
                summary.totalStations(),
                summary.totalEvses(),
                summary.availableConnectors(),
                summary.occupiedConnectors(),
                summary.activeTransactions(),
                summary.todayTransactions()
        );
    }
}
