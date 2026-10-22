package com.workaudit.workaudit.report.controller;

import com.workaudit.workaudit.report.dto.ReportRequest;
import com.workaudit.workaudit.report.dto.ReportResponse;
import com.workaudit.workaudit.report.service.ReportService;

import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<ReportResponse> createReport(
            @Valid @RequestBody ReportRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reportService.createReport(request));
    }

    @GetMapping
    public ResponseEntity<List<ReportResponse>> getAllReports() {

        return ResponseEntity.ok(reportService.getAllReports());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportResponse> getReportById(
            @PathVariable Long id) {

        return ResponseEntity.ok(reportService.getReportById(id));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<ReportResponse>> getReportsByEmployee(
            @PathVariable Long employeeId) {

        return ResponseEntity.ok(
                reportService.getReportsByEmployee(employeeId)
        );
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<ReportResponse>> getReportsByDate(
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {

        return ResponseEntity.ok(
                reportService.getReportsByDate(date)
        );
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<ReportResponse>> getReportsBetweenDates(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        return ResponseEntity.ok(
                reportService.getReportsBetweenDates(startDate, endDate)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReportResponse> updateReport(
            @PathVariable Long id,
            @Valid @RequestBody ReportRequest request) {

        return ResponseEntity.ok(
                reportService.updateReport(id, request)
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ReportResponse> updateReportStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return ResponseEntity.ok(
                reportService.updateReportStatus(id, status)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(
            @PathVariable Long id) {

        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
}