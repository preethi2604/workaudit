package com.workaudit.workaudit.service;

import com.workaudit.workaudit.dto.ReportRequest;
import com.workaudit.workaudit.dto.ReportResponse;
import com.workaudit.workaudit.entity.AuditAction;
import com.workaudit.workaudit.entity.DailyReport;
import com.workaudit.workaudit.entity.Domain;
import com.workaudit.workaudit.entity.Employee;
import com.workaudit.workaudit.entity.ReportStatus;
import com.workaudit.workaudit.repository.DailyReportRepository;
import com.workaudit.workaudit.repository.DomainRepository;
import com.workaudit.workaudit.repository.EmployeeRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ReportService {

    private final DailyReportRepository reportRepository;
    private final EmployeeRepository employeeRepository;
    private final DomainRepository domainRepository;
    private final ReportValidationService validationService;
    private final ReportHistoryService reportHistoryService;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;
    private final CurrentUserAuthorizationService authorizationService;

    public ReportService(
            DailyReportRepository reportRepository,
            EmployeeRepository employeeRepository,
            DomainRepository domainRepository,
            ReportValidationService validationService,
            ReportHistoryService reportHistoryService,
            NotificationService notificationService,
            AuditLogService auditLogService,
            CurrentUserAuthorizationService authorizationService) {

        this.reportRepository = reportRepository;
        this.employeeRepository = employeeRepository;
        this.domainRepository = domainRepository;
        this.validationService = validationService;
        this.reportHistoryService = reportHistoryService;
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
        this.authorizationService = authorizationService;
    }

    /**
     * Create a new daily report.
     */
    public ReportResponse createReport(
            ReportRequest request) {
        return createReportInternal(request, null);
    }

    /**
     * Create a report with a required PDF attachment.
     */
    public ReportResponse createReportWithAttachment(
            ReportRequest request,
            MultipartFile attachment) {
        if (attachment == null || attachment.isEmpty()) {
            throw new IllegalArgumentException(
                    "A PDF attachment is required"
            );
        }
        return createReportInternal(request, attachment);
    }

    private ReportResponse createReportInternal(
            ReportRequest request,
            MultipartFile attachment) {

        authorizationService.requireCanCreateFor(request.getEmployeeId());

        Employee employee =
                employeeRepository.findById(
                        request.getEmployeeId()
                ).orElseThrow(() ->
                        new IllegalArgumentException(
                                "Employee not found: "
                                        + request.getEmployeeId()
                        )
                );

        if (!employee.isActive()) {
            throw new IllegalStateException(
                    "Cannot create report for an inactive employee"
            );
        }

        Domain domain =
                domainRepository.findById(
                        request.getDomainId()
                ).orElseThrow(() ->
                        new IllegalArgumentException(
                                "Domain not found: "
                                        + request.getDomainId()
                        )
                );

        if (!domain.isActive()) {
            throw new IllegalStateException(
                    "Cannot create report for an inactive domain"
            );
        }

        if (reportRepository
                .existsByEmployeeIdAndReportDate(
                        employee.getId(),
                        request.getReportDate()
                )) {

            throw new IllegalStateException(
                    "A report already exists for employee "
                            + employee.getEmployeeCode()
                            + " for date "
                            + request.getReportDate()
            );
        }

        DailyReport report = new DailyReport(
                employee,
                domain,
                request.getReportDate(),
                request.getDailySummary().trim(),
                request.getTotalWorkingMinutes()
        );

        if (attachment != null) {
            applyPdfAttachment(report, attachment);
        }

        DailyReport saved =
                reportRepository.save(report);

        // Audit report creation.
        auditLogService.record(
                getCurrentUserEmail(),
                AuditAction.REPORT_CREATED,
                "REPORT",
                saved.getId(),
                "Created daily report for employee "
                        + saved.getEmployee().getEmployeeCode()
                        + " for date "
                        + saved.getReportDate()
        );

        return ReportResponse.fromEntity(saved);
    }

    /** Update a draft or validation-failed report so the employee can correct it. */
    public ReportResponse updateReport(UUID reportId, ReportRequest request) {
        DailyReport report = getReportEntity(reportId);
        authorizationService.requireReportMutationAccess(report);

        if (!report.getEmployee().getId().equals(request.getEmployeeId())) {
            throw new IllegalArgumentException("The report owner cannot be changed");
        }
        if (request.getReportDate() == null || request.getDomainId() == null
                || request.getDailySummary() == null || request.getTotalWorkingMinutes() == null) {
            throw new IllegalArgumentException("Report date, domain, summary, and working minutes are required");
        }

        Domain domain = domainRepository.findById(request.getDomainId())
                .orElseThrow(() -> new IllegalArgumentException("Domain not found: " + request.getDomainId()));
        if (!domain.isActive()) {
            throw new IllegalStateException("Cannot assign a report to an inactive domain");
        }

        if (!report.getReportDate().equals(request.getReportDate())
                && reportRepository.existsByEmployeeIdAndReportDate(
                        report.getEmployee().getId(), request.getReportDate())) {
            throw new IllegalStateException("A report already exists for this employee and date");
        }

        ReportStatus previousStatus = report.getStatus();
        report.revise(domain, request.getReportDate(), request.getDailySummary().trim(),
                request.getTotalWorkingMinutes());
        DailyReport saved = reportRepository.save(report);

        if (previousStatus != saved.getStatus()) {
            reportHistoryService.recordStatusChange(saved.getId(), previousStatus,
                    saved.getStatus(), "Report edited after validation failure; returned to draft", null);
        }
        auditLogService.record(getCurrentUserEmail(), AuditAction.REPORT_UPDATED,
                "REPORT", saved.getId(), "Updated daily report " + saved.getId());
        return ReportResponse.fromEntity(saved);
    }

    /**
     * Submit a daily report.
     */
    public ReportResponse submitReport(UUID reportId) {

        DailyReport report = getReportEntity(reportId);
        authorizationService.requireReportMutationAccess(report);

        if (!report.hasAttachment()) {
            throw new IllegalStateException(
                    "Upload a PDF attachment before submitting the report"
            );
        }

        ReportStatus previousStatus =
                report.getStatus();

        report.submit();

        DailyReport saved =
                reportRepository.save(report);

        // Audit report submission.
        auditLogService.record(
                getCurrentUserEmail(),
                AuditAction.REPORT_SUBMITTED,
                "REPORT",
                saved.getId(),
                "Submitted daily report for employee "
                        + saved.getEmployee().getEmployeeCode()
                        + " for date "
                        + saved.getReportDate()
        );

        reportHistoryService.recordStatusChange(
                saved.getId(),
                previousStatus,
                saved.getStatus(),
                "Employee submitted daily report",
                null
        );

        validationService.validateReport(saved.getId());

        DailyReport finalReport = reportRepository.findById(saved.getId())
                .orElseThrow(() -> new IllegalStateException("Report disappeared after submission"));

        // Notify managers only when validation did not hard-fail the report.
        if (finalReport.getStatus() != ReportStatus.VALIDATION_FAILED) {
            notificationService.notifyManagersReportSubmitted(finalReport);
        }

        return ReportResponse.fromEntity(finalReport);
    }

    @Transactional(readOnly = true)
    public ReportResponse getReport(
            UUID reportId) {

        DailyReport report = getReportEntity(reportId);
        authorizationService.requireReportReadAccess(report);
        return ReportResponse.fromEntity(report);
    }

    @Transactional(readOnly = true)
    public List<ReportResponse> getEmployeeReports(
            UUID employeeId) {

        if (!employeeRepository.existsById(employeeId)) {
            throw new IllegalArgumentException(
                    "Employee not found: " + employeeId
            );
        }

        return reportRepository
                .findByEmployeeIdOrderByReportDateDesc(
                        employeeId
                )
                .stream()
                .map(ReportResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReportResponse> getEmployeeReportsBetweenDates(
            UUID employeeId,
            LocalDate startDate,
            LocalDate endDate) {

        authorizationService.requireEmployeeScope(employeeId);

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date are required");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "Start date cannot be after end date"
            );
        }

        if (!employeeRepository.existsById(employeeId)) {
            throw new IllegalArgumentException(
                    "Employee not found: " + employeeId
            );
        }

        return reportRepository
                .findByEmployeeIdAndReportDateBetweenOrderByReportDateDesc(
                        employeeId,
                        startDate,
                        endDate
                )
                .stream()
                .map(ReportResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReportResponse> getReportsByDate(
            LocalDate reportDate) {

        if (!authorizationService.isManagerOrAdmin()) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Only managers and administrators can view all reports by date");
        }

        return reportRepository
                .findByReportDate(reportDate)
                .stream()
                .map(ReportResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReportResponse> getReportsByDomainAndDate(
            UUID domainId,
            LocalDate reportDate) {

        if (!authorizationService.isManagerOrAdmin()) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Only managers and administrators can view domain reports");
        }
        if (!domainRepository.existsById(domainId)) {
            throw new IllegalArgumentException(
                    "Domain not found: " + domainId
            );
        }

        return reportRepository
                .findByDomainIdAndReportDate(
                        domainId,
                        reportDate
                )
                .stream()
                .map(ReportResponse::fromEntity)
                .toList();
    }

    public ReportResponse uploadAttachment(
            UUID reportId,
            MultipartFile attachment) {

        DailyReport report = getReportEntity(reportId);
        authorizationService.requireReportMutationAccess(report);

        if (report.getStatus() != ReportStatus.DRAFT) {
            throw new IllegalStateException(
                    "PDF attachment can only be uploaded while the report is in DRAFT status"
            );
        }

        if (attachment == null || attachment.isEmpty()) {
            throw new IllegalArgumentException(
                    "A PDF attachment is required"
            );
        }

        applyPdfAttachment(report, attachment);

        DailyReport saved = reportRepository.save(report);

        auditLogService.record(
                getCurrentUserEmail(),
                AuditAction.REPORT_ATTACHMENT_UPLOADED,
                "REPORT",
                saved.getId(),
                "Uploaded PDF attachment for report "
                        + saved.getId()
        );

        return ReportResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public ReportAttachmentData getReportAttachment(UUID reportId) {
        DailyReport report = getReportEntity(reportId);
        authorizationService.requireReportReadAccess(report);

        if (!report.hasAttachment()) {
            throw new com.workaudit.workaudit.exception.ResourceNotFoundException(
                    "No PDF attachment found for report: " + reportId
            );
        }

        return new ReportAttachmentData(
                report.getAttachmentFileName(),
                report.getAttachmentContentType(),
                report.getAttachmentData()
        );
    }

    private void applyPdfAttachment(
            DailyReport report,
            MultipartFile attachment) {

        final long maxBytes = 10L * 1024L * 1024L;

        if (attachment.getSize() > maxBytes) {
            throw new IllegalArgumentException(
                    "PDF attachment must not exceed 10 MB"
            );
        }

        String originalName = attachment.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            throw new IllegalArgumentException(
                    "PDF filename is required"
            );
        }

        String safeName = java.nio.file.Paths
                .get(originalName.replace('\\', '/'))
                .getFileName()
                .toString();

        if (!safeName.toLowerCase(java.util.Locale.ROOT).endsWith(".pdf")) {
            throw new IllegalArgumentException(
                    "Only PDF attachments are allowed"
            );
        }

        try {
            byte[] data = attachment.getBytes();

            if (data.length < 5
                    || data[0] != '%'
                    || data[1] != 'P'
                    || data[2] != 'D'
                    || data[3] != 'F'
                    || data[4] != '-') {
                throw new IllegalArgumentException(
                        "Uploaded file is not a valid PDF"
                );
            }

            report.setAttachment(
                    safeName,
                    "application/pdf",
                    data
            );
        } catch (java.io.IOException exception) {
            throw new IllegalArgumentException(
                    "Unable to read uploaded PDF",
                    exception
            );
        }
    }

    /**
     * Get the email of the currently authenticated user.
     *
     * The email comes from the JWT/Spring Security context.
     */
    private String getCurrentUserEmail() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            return null;
        }

        return authentication.getName();
    }

    private DailyReport getReportEntity(
            UUID reportId) {

        return reportRepository
                .findById(reportId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Report not found: " + reportId
                        )
                );
    }
}
