package com.charging.adapter.in.web;

import com.charging.adapter.in.web.dto.DashboardSummaryResponse;
import com.charging.domain.port.in.DashboardUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardUseCase dashboardUseCase;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getDashboardSummary() {
        DashboardUseCase.DashboardSummary summary = dashboardUseCase.getDashboardSummary();
        return ResponseEntity.ok(DashboardSummaryResponse.from(summary));
    }
}
