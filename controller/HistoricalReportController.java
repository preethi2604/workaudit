package com.workaudit.workaudit.controller;

import com.workaudit.workaudit.dto.HistoricalReportResponse;
import com.workaudit.workaudit.dto.ReportTrendResponse;
import com.workaudit.workaudit.service.HistoricalReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports/history")
@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
public class HistoricalReportController {

    private final HistoricalReportService historicalReportService;

    public HistoricalReportController(
            HistoricalReportService historicalReportService
    ) {
        this.historicalReportService = historicalReportService;
    }

    /**
     * Complete report history.
     */
    @GetMapping
    public ResponseEntity<List<HistoricalReportResponse>>
    getHistory(

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {

        return ResponseEntity.ok(
                historicalReportService.getHistory(
                        startDate,
                        endDate
                )
        );
    }

    /**
     * Employee historical reports.
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<HistoricalReportResponse>>
    getEmployeeHistory(

            @PathVariable UUID employeeId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {

        return ResponseEntity.ok(
                historicalReportService.getEmployeeHistory(
                        employeeId,
                        startDate,
                        endDate
                )
        );
    }

    /**
     * Domain historical reports.
     */
    @GetMapping("/domain/{domainId}")
    public ResponseEntity<List<HistoricalReportResponse>>
    getDomainHistory(

            @PathVariable UUID domainId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {

        return ResponseEntity.ok(
                historicalReportService.getDomainHistory(
                        domainId,
                        startDate,
                        endDate
                )
        );
    }

    /**
     * Daily report trend data for frontend charts.
     */
    @GetMapping("/trends")
    public ResponseEntity<List<ReportTrendResponse>>
    getDailyTrends(

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {

        return ResponseEntity.ok(
                historicalReportService.getDailyTrends(
                        startDate,
                        endDate
                )
        );
    }
}