package com.workaudit.workaudit.controller;

import com.workaudit.workaudit.dto.EmployeeReportSummaryResponse;
import com.workaudit.workaudit.dto.ReportResponse;
import com.workaudit.workaudit.service.EmployeeReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports/employee")
public class EmployeeReportController {

    private final EmployeeReportService employeeReportService;

    public EmployeeReportController(
            EmployeeReportService employeeReportService
    ) {
        this.employeeReportService = employeeReportService;
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<List<ReportResponse>> getEmployeeReports(
            @PathVariable UUID employeeId
    ) {

        return ResponseEntity.ok(
                employeeReportService.getEmployeeReports(
                        employeeId
                )
        );
    }

    @GetMapping("/{employeeId}/range")
    public ResponseEntity<List<ReportResponse>> getEmployeeReportsByDateRange(
            @PathVariable UUID employeeId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {

        return ResponseEntity.ok(
                employeeReportService
                        .getEmployeeReportsByDateRange(
                                employeeId,
                                startDate,
                                endDate
                        )
        );
    }

    @GetMapping("/{employeeId}/summary")
    public ResponseEntity<EmployeeReportSummaryResponse>
    getEmployeeSummary(
            @PathVariable UUID employeeId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {

        return ResponseEntity.ok(
                employeeReportService.getEmployeeSummary(
                        employeeId,
                        startDate,
                        endDate
                )
        );
    }
}