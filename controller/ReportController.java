package com.workaudit.workaudit.controller;

import com.workaudit.workaudit.dto.ReportRequest;
import com.workaudit.workaudit.dto.ReportResponse;
import com.workaudit.workaudit.service.ReportService;
import com.workaudit.workaudit.service.ReportAttachmentData;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestPart;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(
            ReportService reportService
    ) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<ReportResponse> createReport(
            @Valid @RequestBody ReportRequest request
    ) {

        ReportResponse response =
                reportService.createReport(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Create a daily report with a PDF attachment.
     *
     * Postman: Body -> form-data
     * report: Text field containing the JSON report request
     * attachment: File field containing a PDF
     */
    @PostMapping(
            value = "/with-attachment",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ReportResponse> createReportWithAttachment(
            @Valid @RequestPart("report") ReportRequest request,
            @RequestPart("attachment") MultipartFile attachment) {

        ReportResponse response =
                reportService.createReportWithAttachment(
                        request,
                        attachment
                );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Upload or replace the PDF for an existing draft report.
     */
    @PutMapping(
            value = "/{reportId}/attachment",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ReportResponse> uploadAttachment(
            @PathVariable UUID reportId,
            @RequestPart("attachment") MultipartFile attachment) {

        return ResponseEntity.ok(
                reportService.uploadAttachment(reportId, attachment)
        );
    }

    /**
     * Download the PDF attached to a report.
     */
    @GetMapping("/{reportId}/attachment")
    public ResponseEntity<byte[]> downloadAttachment(
            @PathVariable UUID reportId) {

        ReportAttachmentData attachment =
                reportService.getReportAttachment(reportId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + sanitizeDownloadFileName(attachment.fileName()) + "\""
                )
                .contentLength(attachment.data().length)
                .body(attachment.data());
    }

    @PutMapping("/{reportId}")
    public ResponseEntity<ReportResponse> updateReport(
            @PathVariable UUID reportId,
            @Valid @RequestBody ReportRequest request) {
        return ResponseEntity.ok(reportService.updateReport(reportId, request));
    }

    @PostMapping("/{reportId}/submit")
    public ResponseEntity<ReportResponse> submitReport(
            @PathVariable UUID reportId
    ) {

        return ResponseEntity.ok(
                reportService.submitReport(reportId)
        );
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<ReportResponse> getReport(
            @PathVariable UUID reportId
    ) {

        return ResponseEntity.ok(
                reportService.getReport(reportId)
        );
    }



    @GetMapping("/employee/{employeeId}/history")
    public ResponseEntity<List<ReportResponse>> getEmployeeReportHistory(
            @PathVariable UUID employeeId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {

        return ResponseEntity.ok(
                reportService.getEmployeeReportsBetweenDates(
                        employeeId,
                        startDate,
                        endDate
                )
        );
    }

    @GetMapping("/date/{reportDate}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<List<ReportResponse>> getReportsByDate(
            @PathVariable LocalDate reportDate
    ) {

        return ResponseEntity.ok(
                reportService.getReportsByDate(
                        reportDate
                )
        );
    }
    private String sanitizeDownloadFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return "daily-report.pdf";
        }
        // Prevent response-header injection and remove path components from legacy DB values.
        String sanitized = fileName.replace('\\', '/');
        sanitized = sanitized.substring(sanitized.lastIndexOf('/') + 1)
                .replaceAll("[\\r\\n\"]", "");
        return sanitized.isBlank() ? "daily-report.pdf" : sanitized;
    }

}