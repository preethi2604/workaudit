package com.workaudit.workaudit.report.service;

import com.workaudit.workaudit.report.dto.DuplicateMatch;
import com.workaudit.workaudit.report.dto.ExtractedReportData;
import com.workaudit.workaudit.report.dto.ReportErrorResponse;
import com.workaudit.workaudit.report.dto.ReportProcessingResult;
import com.workaudit.workaudit.report.dto.ReportUploadRequest;
import com.workaudit.workaudit.report.entity.DailyReport;
import com.workaudit.workaudit.report.entity.DescriptionLevel;
import com.workaudit.workaudit.report.entity.ErrorType;
import com.workaudit.workaudit.report.entity.ManagerMeeting;
import com.workaudit.workaudit.report.entity.ReportDuplicate;
import com.workaudit.workaudit.report.entity.ReportError;
import com.workaudit.workaudit.report.entity.ReportHourlyEntry;
import com.workaudit.workaudit.report.entity.ReportStatus;
import com.workaudit.workaudit.report.entity.ReportValidationResult;
import com.workaudit.workaudit.report.repository.DailyReportRepository;
import com.workaudit.workaudit.report.repository.ReportDuplicateRepository;
import com.workaudit.workaudit.report.repository.ReportErrorRepository;
import com.workaudit.workaudit.report.repository.ReportValidationResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportProcessingService {


    private static final boolean MEETING_REQUIRED_DAILY = false;


    private record Finding(ErrorType type, String message) {
    }

    private final PdfExtractionService pdfExtractionService;
    private final PdfStorageService pdfStorageService;
    private final WorkingHoursService workingHoursService;
    private final DateValidationService dateValidationService;
    private final DescriptionValidationService descriptionValidationService;
    private final MeetingValidationService meetingValidationService;
    private final DuplicateDetectionService duplicateDetectionService;

    private final DailyReportRepository dailyReportRepository;
    private final ReportValidationResultRepository validationResultRepository;
    private final ReportErrorRepository reportErrorRepository;
    private final ReportDuplicateRepository reportDuplicateRepository;

    @Autowired
    public ReportProcessingService(PdfExtractionService pdfExtractionService,
                                   PdfStorageService pdfStorageService,
                                   WorkingHoursService workingHoursService,
                                   DateValidationService dateValidationService,
                                   DescriptionValidationService descriptionValidationService,
                                   MeetingValidationService meetingValidationService,
                                   DuplicateDetectionService duplicateDetectionService,
                                   DailyReportRepository dailyReportRepository,
                                   ReportValidationResultRepository validationResultRepository,
                                   ReportErrorRepository reportErrorRepository,
                                   ReportDuplicateRepository reportDuplicateRepository) {
        this.pdfExtractionService = pdfExtractionService;
        this.pdfStorageService = pdfStorageService;
        this.workingHoursService = workingHoursService;
        this.dateValidationService = dateValidationService;
        this.descriptionValidationService = descriptionValidationService;
        this.meetingValidationService = meetingValidationService;
        this.duplicateDetectionService = duplicateDetectionService;
        this.dailyReportRepository = dailyReportRepository;
        this.validationResultRepository = validationResultRepository;
        this.reportErrorRepository = reportErrorRepository;
        this.reportDuplicateRepository = reportDuplicateRepository;
    }

    @Transactional
    public ReportProcessingResult processUpload(MultipartFile pdfFile, ReportUploadRequest request) {
        String employeeId = request.getEmployeeId();
        LocalDate expectedDate = request.getExpectedDate();

        // ---- 1. Find the existing report for this employee and day, or start a new one ----
        DailyReport report = dailyReportRepository
                .findByEmployeeIdAndExpectedDate(employeeId, expectedDate)
                .orElseGet(() -> new DailyReport(employeeId, expectedDate));

        if (report.getId() != null) {
            // re-upload: throw away the old results, they are about to be recomputed
            reportErrorRepository.deleteByReportId(report.getId());
            reportDuplicateRepository.deleteByReportId(report.getId());
        }

        report.setSubmittedAt(LocalDateTime.now());
        report.setDomainId(request.getDomainId());
        report.setProjectId(request.getProjectId());

        // ---- 2. Keep the PDF, then read it ----
        report.setPdfPath(pdfStorageService.store(pdfFile, employeeId, expectedDate));
        ExtractedReportData extracted = pdfExtractionService.extract(pdfFile);

        System.out.println("===== RAW PDF TEXT START =====");
        System.out.println(extracted.getRawText());
        System.out.println("===== RAW PDF TEXT END =====");
        System.out.println("===== PARSED ROWS =====");
        for (var row : extracted.getHourlyEntries()) {
            System.out.println("slot=" + row.getSlotStart() + "-" + row.getSlotEnd()
                    + " | start=" + row.getStartTime() + " end=" + row.getEndTime()
                    + " | isBreak=" + row.isBreak()
                    + " | desc=" + row.getDescription());
        }
        System.out.println("===== END PARSED ROWS =====");

        if (extracted.isParseError()) {
            return handleParseError(report, extracted, expectedDate);
        }

        List<Finding> findings = new ArrayList<>();

        report.setReportDate(extracted.getReportDate());
        report.setDailySummary(extracted.getDailySummary());

        // ---- 3. Is this the right employee's report? ----
        if (extracted.getEmployeeId() != null
                && !extracted.getEmployeeId().equalsIgnoreCase(employeeId)) {
            findings.add(new Finding(ErrorType.EMPLOYEE_DOMAIN_MISMATCH,
                    "The PDF is for employee " + extracted.getEmployeeId()
                            + " but it was uploaded for " + employeeId));
        }

        // ---- 4. Report date and submission time ----
        boolean dateOk = dateValidationService.checkReportDate(extracted.getReportDate(), expectedDate)
                == DateValidationService.DateCheckResult.MATCHES;
        if (!dateOk) {
            findings.add(new Finding(ErrorType.WRONG_REPORT_DATE,
                    "Report is dated " + extracted.getReportDate() + " but was expected for " + expectedDate));
        }

        boolean late = dateValidationService.isLate(extracted.getReportDate(), report.getSubmittedAt());
        if (late) {
            findings.add(new Finding(ErrorType.LATE_SUBMISSION,
                    "Report for " + extracted.getReportDate() + " was submitted on "
                            + report.getSubmittedAt().toLocalDate()));
        }

        // ---- 5. Working hours and login time ----
        double totalHours = workingHoursService.calculateTotalHours(extracted);
        WorkingHoursService.HoursResult hoursResult = workingHoursService.classify(totalHours);
        report.setTotalWorkingHours(totalHours);

        if (!hoursResult.isMeetsRequirement()) {
            findings.add(new Finding(ErrorType.BELOW_REQUIRED_HOURS,
                    String.format("Worked %.1f hours", totalHours)));
        }
        if (workingHoursService.isLateLogin(extracted)) {
            findings.add(new Finding(ErrorType.LATE_LOGIN,
                    "First login at " + workingHoursService.getLoginTime(extracted)));
        }

        // ---- 6. Task rows and their descriptions ----
        boolean anyWorkRow = extracted.getHourlyEntries().stream().anyMatch(r -> !r.isBreak());
        if (!anyWorkRow) {
            findings.add(new Finding(ErrorType.HOURLY_DESCRIPTION_MISSING, null));
        }

        report.getHourlyEntries().clear();
        int poorRows = 0;
        for (ExtractedReportData.HourlyEntry row : extracted.getHourlyEntries()) {
            ReportHourlyEntry entry = new ReportHourlyEntry(
                    report, row.getStartTime(), row.getEndTime(), row.getDescription());

            boolean poor = !row.isBreak()
                    && descriptionValidationService.validateEntry(row.getDescription()) != DescriptionLevel.GREEN;
            entry.setFlaggedPoor(poor);
            if (poor) {
                poorRows++;
            }
            report.getHourlyEntries().add(entry);
        }

        DescriptionLevel summaryLevel = descriptionValidationService.validateDailySummary(extracted.getDailySummary());
        if (summaryLevel == DescriptionLevel.RED) {
            findings.add(new Finding(ErrorType.DAILY_SUMMARY_MISSING, null));
        }
        boolean poorDescription = summaryLevel == DescriptionLevel.YELLOW || poorRows > 0;
        if (poorDescription) {
            findings.add(new Finding(ErrorType.POOR_DESCRIPTION,
                    poorRows + " task row(s) have a short or generic description"
                            + (summaryLevel == DescriptionLevel.YELLOW ? "; the detailed summary is too short" : "")));
        }
        DescriptionLevel descriptionLevel = descriptionValidationService.validateOverall(extracted);

        // ---- 7. Manager meeting ----
        MeetingValidationService.MeetingValidationOutcome meetingOutcome =
                meetingValidationService.validate(extracted.getMeetingSummary());

        boolean meetingOk;
        if (!meetingOutcome.isPresent()) {
            meetingOk = !MEETING_REQUIRED_DAILY;
        } else {
            meetingOk = meetingOutcome.isComplete();
        }
        if (!meetingOk) {
            findings.add(new Finding(ErrorType.MEETING_SUMMARY_MISSING, meetingOutcome.getMissingFieldsSummary()));
        }

        if (meetingOutcome.isPresent()) {
            ExtractedReportData.MeetingSummary summary = extracted.getMeetingSummary();
            ManagerMeeting meeting = report.getManagerMeeting();
            if (meeting == null) {
                meeting = new ManagerMeeting(report);
                report.setManagerMeeting(meeting);
            }
            meeting.setWasPresent(true);
            meeting.setMeetingTime(summary.getMeetingTime());
            meeting.setManagerUpdates(summary.getDiscussion());
        } else {
            report.setManagerMeeting(null);
        }

        // ---- 8. Duplicate and repeated content ----
        String combinedText = buildCombinedText(extracted);
        report.setContentHash(duplicateDetectionService.sha256(combinedText));

        List<DailyReport> peers = dailyReportRepository.findByExpectedDateAndEmployeeIdNot(expectedDate, employeeId);
        List<DuplicateMatch> peerMatches = duplicateDetectionService.checkAgainstPeers(
                employeeId, combinedText, toCandidates(peers));

        List<DailyReport> history = dailyReportRepository.findByEmployeeIdOrderByExpectedDateDesc(employeeId).stream()
                .filter(r -> r.getExpectedDate().isBefore(expectedDate))
                .collect(Collectors.toList());
        List<DuplicateMatch> selfMatches = duplicateDetectionService.checkAgainstOwnHistory(
                combinedText, toCandidates(history));

        if (!peerMatches.isEmpty()) {
            int highest = peerMatches.stream().mapToInt(DuplicateMatch::getSimilarityPercentage).max().orElse(0);
            findings.add(new Finding(ErrorType.DUPLICATE_REPORT,
                    "Similar to " + peerMatches.size() + " other employee report(s), highest match " + highest + "%"));
        }
        if (!selfMatches.isEmpty()) {
            findings.add(new Finding(ErrorType.REPEATED_DESCRIPTION,
                    "Same description as the previous " + selfMatches.size() + " day(s)"));
        }
        boolean duplicateFlagged = !peerMatches.isEmpty() || !selfMatches.isEmpty();

        // ---- 9. Decide the status the manager will see ----
        ReportStatus status;
        if (findings.isEmpty()) {
            status = ReportStatus.SUBMITTED;
        } else if (!dateOk) {
            status = ReportStatus.DATE_MISMATCH;
        } else if (duplicateFlagged) {
            status = ReportStatus.DUPLICATE_SUSPECTED;
        } else if (poorDescription) {
            status = ReportStatus.POOR_DESCRIPTION;
        } else {
            status = ReportStatus.NEED_CORRECTION;
        }
        report.setStatus(status);

        // ---- 10. Save everything ----
        DailyReport saved = dailyReportRepository.save(report);

        persistFindings(saved, findings);

        List<DuplicateMatch> allMatches = new ArrayList<>(peerMatches);
        allMatches.addAll(selfMatches);
        List<ReportDuplicate> duplicateRows = new ArrayList<>();
        for (DuplicateMatch match : allMatches) {
            duplicateRows.add(new ReportDuplicate(saved, match.getComparedReportId(), match.getComparedEmployeeId(),
                    match.getSimilarityScore(), ReportDuplicate.MatchType.valueOf(match.getMatchType().name())));
        }
        reportDuplicateRepository.saveAll(duplicateRows);

        boolean overallPass = findings.isEmpty();
        saveValidationResult(saved, descriptionLevel, hoursResult.isMeetsRequirement(),
                dateOk, meetingOk, duplicateFlagged, overallPass);

        return buildResult(saved, totalHours, hoursResult.isMeetsRequirement(), dateOk, late,
                descriptionLevel, meetingOk, duplicateFlagged, false, null, findings);
    }

    // ------------------------------------------------------------------ helpers

    /** The PDF could not be read: keep the file, record why, and stop. */
    private ReportProcessingResult handleParseError(DailyReport report, ExtractedReportData extracted,
                                                    LocalDate expectedDate) {
        report.setReportDate(expectedDate); // the column cannot be empty, and the real date is unknown
        report.setStatus(ReportStatus.PARSE_ERROR);
        DailyReport saved = dailyReportRepository.save(report);

        List<Finding> findings = List.of(new Finding(ErrorType.INVALID_FORMAT, extracted.getParseErrorMessage()));
        persistFindings(saved, findings);

        saveValidationResult(saved, DescriptionLevel.RED, false, false, false, false, false);

        return buildResult(saved, 0.0, false, false, false, DescriptionLevel.RED,
                false, false, true, extracted.getParseErrorMessage(), findings);
    }

    private void persistFindings(DailyReport saved, List<Finding> findings) {
        List<ReportError> rows = new ArrayList<>();
        for (Finding f : findings) {
            String message = (f.message() != null && !f.message().isBlank())
                    ? f.message()
                    : f.type().getDefaultMessage();
            rows.add(new ReportError(saved, f.type(), message));
        }
        reportErrorRepository.saveAll(rows);
    }

    private void saveValidationResult(DailyReport saved, DescriptionLevel level, boolean hoursOk,
                                      boolean dateOk, boolean meetingOk, boolean duplicateFlagged,
                                      boolean overallPass) {
        ReportValidationResult result = validationResultRepository.findByReportId(saved.getId())
                .orElseGet(() -> new ReportValidationResult(saved));
        result.setDescriptionLevel(level);
        result.setHoursOk(hoursOk);
        result.setDateOk(dateOk);
        result.setMeetingOk(meetingOk);
        result.setDuplicateFlagged(duplicateFlagged);
        result.setOverallPass(overallPass);
        result.setComputedAt(LocalDateTime.now());
        validationResultRepository.save(result);
    }

    private List<DuplicateDetectionService.ComparisonCandidate> toCandidates(List<DailyReport> reports) {
        return reports.stream()
                .map(r -> new DuplicateDetectionService.ComparisonCandidate(
                        r.getId(), r.getEmployeeId(), r.getExpectedDate(), buildCombinedText(r)))
                .collect(Collectors.toList());
    }

    /** Detailed summary plus every task description, used for similarity checks. */
    private String buildCombinedText(ExtractedReportData extracted) {
        StringBuilder sb = new StringBuilder();
        if (extracted.getDailySummary() != null) {
            sb.append(extracted.getDailySummary()).append(' ');
        }
        for (ExtractedReportData.HourlyEntry row : extracted.getHourlyEntries()) {
            if (row.getDescription() != null) {
                sb.append(row.getDescription()).append(' ');
            }
        }
        return sb.toString().trim();
    }

    private String buildCombinedText(DailyReport report) {
        StringBuilder sb = new StringBuilder();
        if (report.getDailySummary() != null) {
            sb.append(report.getDailySummary()).append(' ');
        }
        for (ReportHourlyEntry entry : report.getHourlyEntries()) {
            if (entry.getDescription() != null) {
                sb.append(entry.getDescription()).append(' ');
            }
        }
        return sb.toString().trim();
    }

    private ReportProcessingResult buildResult(DailyReport saved, double totalHours, boolean hoursOk,
                                               boolean dateOk, boolean late, DescriptionLevel descriptionLevel,
                                               boolean meetingOk, boolean duplicateFlagged,
                                               boolean parseError, String parseErrorMessage,
                                               List<Finding> findings) {
        ReportProcessingResult result = new ReportProcessingResult();
        result.setReportId(saved.getId());
        result.setEmployeeId(saved.getEmployeeId());
        result.setStatus(saved.getStatus());
        result.setTotalWorkingHours(totalHours);
        result.setHoursOk(hoursOk);
        result.setDateOk(dateOk);
        result.setLateSubmission(late);
        result.setDescriptionLevel(descriptionLevel);
        result.setMeetingOk(meetingOk);
        result.setDuplicateFlagged(duplicateFlagged);
        result.setParseError(parseError);
        result.setParseErrorMessage(parseErrorMessage);
        result.setErrors(findings.stream()
                .map(f -> new ReportErrorResponse(f.type(),
                        (f.message() != null && !f.message().isBlank()) ? f.message() : f.type().getDefaultMessage()))
                .collect(Collectors.toList()));
        return result;
    }
}