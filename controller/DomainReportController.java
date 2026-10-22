package com.workaudit.workaudit.controller;

import com.workaudit.workaudit.dto.DomainReportSummaryResponse;
import com.workaudit.workaudit.dto.ReportResponse;
import com.workaudit.workaudit.service.DomainReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports/domain")
@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
public class DomainReportController {

    private final DomainReportService domainReportService;

    public DomainReportController(
            DomainReportService domainReportService
    ) {
        this.domainReportService = domainReportService;
    }

    @GetMapping("/{domainId}")
    public ResponseEntity<List<ReportResponse>> getDomainReports(
            @PathVariable UUID domainId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate reportDate
    ) {

        return ResponseEntity.ok(
                domainReportService.getDomainReports(
                        domainId,
                        reportDate
                )
        );
    }

    @GetMapping("/{domainId}/summary")
    public ResponseEntity<DomainReportSummaryResponse>
    getDomainSummary(
            @PathVariable UUID domainId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {

        return ResponseEntity.ok(
                domainReportService.getDomainSummary(
                        domainId,
                        startDate,
                        endDate
                )
        );
    }
}