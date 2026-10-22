package com.workaudit.workaudit.controller;

import com.workaudit.workaudit.dto.ReviewReportRequest;
import com.workaudit.workaudit.dto.ReviewReportResponse;
import com.workaudit.workaudit.entity.Employee;
import com.workaudit.workaudit.exception.ResourceNotFoundException;
import com.workaudit.workaudit.repository.EmployeeRepository;
import com.workaudit.workaudit.service.ReportReviewService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
public class ReportReviewController {

    private final ReportReviewService reportReviewService;
    private final EmployeeRepository employeeRepository;

    public ReportReviewController(
            ReportReviewService reportReviewService,
            EmployeeRepository employeeRepository) {

        this.reportReviewService = reportReviewService;
        this.employeeRepository = employeeRepository;
    }

    /**
     * Move a submitted report to manager review.
     *
     * Only MANAGER and ADMIN can review reports.
     */
    @PostMapping("/{reportId}/review")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ReviewReportResponse> moveToReview(
            @PathVariable UUID reportId,
            Authentication authentication) {

        UUID reviewerId = getAuthenticatedEmployeeId(authentication);

        ReviewReportResponse response =
                reportReviewService.moveToReview(
                        reportId,
                        reviewerId
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Approve a report.
     */
    @PostMapping("/{reportId}/approve")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ReviewReportResponse> approveReport(
            @PathVariable UUID reportId,
            @Valid @RequestBody ReviewReportRequest request,
            Authentication authentication) {

        UUID reviewerId = getAuthenticatedEmployeeId(authentication);

        ReviewReportResponse response =
                reportReviewService.approveReport(
                        reportId,
                        request,
                        reviewerId
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Reject a report.
     */
    @PostMapping("/{reportId}/reject")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ReviewReportResponse> rejectReport(
            @PathVariable UUID reportId,
            @Valid @RequestBody ReviewReportRequest request,
            Authentication authentication) {

        UUID reviewerId = getAuthenticatedEmployeeId(authentication);

        ReviewReportResponse response =
                reportReviewService.rejectReport(
                        reportId,
                        request,
                        reviewerId
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Resolve the authenticated employee from the JWT identity.
     *
     * Never accept the reviewer ID directly from the request body.
     */
    private UUID getAuthenticatedEmployeeId(
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null) {

            throw new org.springframework.security.access.AccessDeniedException(
                    "An authenticated user is required"
            );
        }

        Employee employee = employeeRepository
                .findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated employee was not found"
                        )
                );

        if (!employee.isActive()) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Inactive employees cannot review reports"
            );
        }

        return employee.getId();
    }
}