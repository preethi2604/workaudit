package com.workaudit.workaudit.controller;

import com.workaudit.workaudit.dto.ReportHistoryResponse;
import com.workaudit.workaudit.service.ReportHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
public class ReportHistoryController {

    private final ReportHistoryService reportHistoryService;

    public ReportHistoryController(
            ReportHistoryService reportHistoryService
    ) {
        this.reportHistoryService = reportHistoryService;
    }

    /**
     * Get complete status history of a report.
     */
    @GetMapping("/{reportId}/history")
    public ResponseEntity<List<ReportHistoryResponse>> getReportHistory(
            @PathVariable UUID reportId
    ) {

        return ResponseEntity.ok(
                reportHistoryService.getReportHistory(reportId)
        );
    }
}