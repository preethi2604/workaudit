package com.workaudit.workaudit.controller;

import com.workaudit.workaudit.dto.DashboardResponse;
import com.workaudit.workaudit.dto.DomainDashboardResponse;
import com.workaudit.workaudit.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(
            DashboardService dashboardService
    ) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardResponse> getSummary() {

        return ResponseEntity.ok(
                dashboardService.getSummary()
        );
    }

    @GetMapping("/domains")
    public ResponseEntity<List<DomainDashboardResponse>>
    getDomainStatistics() {

        return ResponseEntity.ok(
                dashboardService.getDomainStatistics()
        );
    }
}