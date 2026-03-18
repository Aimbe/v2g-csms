package com.charging.domain.entity;

import com.charging.domain.port.in.DashboardUseCase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "DASHBOARD_PROJECTION")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DashboardProjection extends BaseEntity {

    public static final String GLOBAL_KEY = "GLOBAL";

    @Id
    @Column(name = "projection_key", length = 50, nullable = false)
    private String projectionKey;

    @Column(name = "total_stations", nullable = false)
    private long totalStations;

    @Column(name = "total_evses", nullable = false)
    private long totalEvses;

    @Column(name = "available_connectors", nullable = false)
    private long availableConnectors;

    @Column(name = "occupied_connectors", nullable = false)
    private long occupiedConnectors;

    @Column(name = "active_transactions", nullable = false)
    private long activeTransactions;

    @Column(name = "today_transactions", nullable = false)
    private long todayTransactions;

    public void update(DashboardUseCase.DashboardSummary summary) {
        this.totalStations = summary.totalStations();
        this.totalEvses = summary.totalEvses();
        this.availableConnectors = summary.availableConnectors();
        this.occupiedConnectors = summary.occupiedConnectors();
        this.activeTransactions = summary.activeTransactions();
        this.todayTransactions = summary.todayTransactions();
    }

    public DashboardUseCase.DashboardSummary toSummary() {
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
