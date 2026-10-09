package com.workaudit.workaudit.report.controller;

import com.workaudit.workaudit.report.dto.ReportErrorResponse;
import com.workaudit.workaudit.report.dto.ReportProcessingResult;
import com.workaudit.workaudit.report.dto.ReportUploadRequest;
import com.workaudit.workaudit.report.dto.ValidationResultResponse;
import com.workaudit.workaudit.report.entity.ErrorType;
import com.workaudit.workaudit.report.entity.ReportError;
import com.workaudit.workaudit.report.repository.ReportErrorRepository;
import com.workaudit.workaudit.report.repository.ReportValidationResultRepository;
import com.workaudit.workaudit.report.service.MissingReportService;
import com.workaudit.workaudit.report.service.ReportProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
public class ReportController {


    public record FlaggedReport(Long reportId, String employeeId, LocalDate expectedDate,
                                ErrorType errorType, String message) {
    }


    public record MissingReportItem(String employeeId, String employeeName,
                                    String domainName, LocalDate expectedDate) {
    }

    private final ReportProcessingService reportProcessingService;
    private final ReportValidationResultRepository validationResultRepository;
    private final ReportErrorRepository reportErrorRepository;
    private final MissingReportService missingReportService;

    @Autowired
    public ReportController(ReportProcessingService reportProcessingService,
                            ReportValidationResultRepository validationResultRepository,
                            ReportErrorRepository reportErrorRepository,
                            MissingReportService missingReportService) {
        this.reportProcessingService = reportProcessingService;
        this.validationResultRepository = validationResultRepository;
        this.reportErrorRepository = reportErrorRepository;
        this.missingReportService = missingReportService;
    }


    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<ReportProcessingResult> uploadReport(
            @RequestParam("file") MultipartFile file,
            @RequestParam("employeeId") String employeeId,
            @RequestParam("expectedDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expectedDate,
            @RequestParam(value = "domainId", required = false) Long domainId,
            @RequestParam(value = "projectId", required = false) Long projectId) {

        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The uploaded file is empty");
        }
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.toLowerCase().endsWith(".pdf")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only PDF files are accepted");
        }
        if (employeeId == null || employeeId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "employeeId is required");
        }

        ReportUploadRequest request = new ReportUploadRequest();
        request.setEmployeeId(employeeId.trim());
        request.setExpectedDate(expectedDate);
        request.setDomainId(domainId);
        request.setProjectId(projectId);

        ReportProcessingResult result = reportProcessingService.processUpload(file, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }


    @GetMapping("/{reportId}/validation")
    @Transactional(readOnly = true)
    public ResponseEntity<ValidationResultResponse> getValidationResult(@PathVariable Long reportId) {
        return validationResultRepository.findByReportId(reportId)
                .map(result -> {
                    ValidationResultResponse response = new ValidationResultResponse();
                    response.setReportId(reportId);
                    response.setDescriptionLevel(result.getDescriptionLevel());
                    response.setHoursOk(result.isHoursOk());
                    response.setDateOk(result.isDateOk());
                    response.setMeetingOk(result.isMeetingOk());
                    response.setDuplicateFlagged(result.isDuplicateFlagged());
                    response.setOverallPass(result.isOverallPass());
                    response.setComputedAt(result.getComputedAt());
                    response.setErrors(reportErrorRepository.findByReportId(reportId).stream()
                            .map(e -> new ReportErrorResponse(e.getErrorType(), e.getMessage()))
                            .collect(Collectors.toList()));
                    return ResponseEntity.ok(response);
                })
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No validation result for report " + reportId));
    }

    @GetMapping("/{reportId}/errors")
    public ResponseEntity<List<ReportErrorResponse>> getReportErrors(@PathVariable Long reportId) {
        List<ReportErrorResponse> errors = reportErrorRepository.findByReportId(reportId).stream()
                .map(e -> new ReportErrorResponse(e.getErrorType(), e.getMessage()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(errors);
    }



    @GetMapping("/late")
    @Transactional(readOnly = true)
    public ResponseEntity<List<FlaggedReport>> getLateReports(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(flaggedBy(date, ErrorType.LATE_SUBMISSION));
    }

    @GetMapping("/below-hours")
    @Transactional(readOnly = true)
    public ResponseEntity<List<FlaggedReport>> getBelowHoursReports(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(flaggedBy(date, ErrorType.BELOW_REQUIRED_HOURS));
    }

    @GetMapping("/wrong-date")
    @Transactional(readOnly = true)
    public ResponseEntity<List<FlaggedReport>> getWrongDateReports(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(flaggedBy(date, ErrorType.WRONG_REPORT_DATE));
    }


    @GetMapping("/duplicate")
    @Transactional(readOnly = true)
    public ResponseEntity<List<FlaggedReport>> getDuplicateReports(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<FlaggedReport> flagged = reportErrorRepository.findByReport_ExpectedDate(date).stream()
                .filter(e -> e.getErrorType() == ErrorType.DUPLICATE_REPORT
                        || e.getErrorType() == ErrorType.REPEATED_DESCRIPTION)
                .map(this::toFlagged)
                .collect(Collectors.toList());
        return ResponseEntity.ok(flagged);
    }


    @GetMapping("/incorrect")
    @Transactional(readOnly = true)
    public ResponseEntity<List<FlaggedReport>> getIncorrectReports(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<FlaggedReport> flagged = reportErrorRepository.findByReport_ExpectedDate(date).stream()
                .map(this::toFlagged)
                .collect(Collectors.toList());
        return ResponseEntity.ok(flagged);
    }


    @GetMapping("/missing")
    public ResponseEntity<List<MissingReportItem>> getMissingReports(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<MissingReportItem> missing = missingReportService.findMissingEmployees(date).stream()
                .map(m -> new MissingReportItem(m.getEmployeeId(), m.getEmployeeName(), m.getDomainName(), date))
                .collect(Collectors.toList());
        return ResponseEntity.ok(missing);
    }



    private List<FlaggedReport> flaggedBy(LocalDate date, ErrorType type) {
        return reportErrorRepository.findByReport_ExpectedDateAndErrorType(date, type).stream()
                .map(this::toFlagged)
                .collect(Collectors.toList());
    }

    private FlaggedReport toFlagged(ReportError error) {
        return new FlaggedReport(
                error.getReport().getId(),
                error.getReport().getEmployeeId(),
                error.getReport().getExpectedDate(),
                error.getErrorType(),
                error.getMessage());
    }
}