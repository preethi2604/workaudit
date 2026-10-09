package com.workaudit.workaudit.report.service;

import com.workaudit.workaudit.report.dto.ExtractedReportData;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PdfExtractionService {

    private static final Pattern EMPLOYEE_ID = Pattern.compile("Employee\\s*ID\\s*:\\s*(\\S+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern EMPLOYEE_NAME = Pattern.compile("Employee\\s*Name\\s*:\\s*(.+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern DEPARTMENT = Pattern.compile("Department\\s*/\\s*Team\\s*:\\s*(.+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern SHIFT_TIME = Pattern.compile("Shift\\s*Time\\s*:\\s*(.+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern REPORT_DATE = Pattern.compile("\\bDate\\s*:\\s*(\\d{1,2})[/-](\\d{1,2})[/-](\\d{4})", Pattern.CASE_INSENSITIVE);

    private static final Pattern ROW_START = Pattern.compile(
            "^\\s*(\\d{1,2}:\\d{2})\\s*[-\u2013]\\s*(\\d{1,2}:\\d{2})\\s*(.*)$");

    private static final Pattern ROW_END = Pattern.compile(
            "^(.*?)\\s*(\\d{1,2}:\\d{2})\\s+(\\d{1,2}:\\d{2})\\s+(\\S+)\\s+(Completed|Pending|In\\s*Progress)\\s*$",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern DURATION = Pattern.compile(
            "(?:(\\d+)\\s*hours?)?\\s*(?:(\\d+)\\s*(?:mnt|min|mins|minutes?))?", Pattern.CASE_INSENSITIVE);


    private static final Pattern MEETING_TIME_RANGE = Pattern.compile(
            "(\\d{1,2}:\\d{2})\\s*(?:to|-|\u2013)\\s*(\\d{1,2}:\\d{2})", Pattern.CASE_INSENSITIVE);


    public ExtractedReportData extract(MultipartFile pdfFile) {
        ExtractedReportData result = new ExtractedReportData();

        try (PDDocument document = Loader.loadPDF(pdfFile.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document).replace("\r", "");


            text = text.replaceAll("(?i)(Completed|Pending|In\\s*Progress)(?=\\d{1,2}:\\d{2}\\s*[-\u2013])", "$1\n");

            result.setRawText(text);

            parseEmployeeDetails(text, result);
            parseTaskTable(text, result);
            parseSections(text, result);
            detectMeeting(result);

            if (result.getEmployeeId() == null) {
                result.setParseError(true);
                result.setParseErrorMessage("Employee ID not found in the PDF");
            } else if (result.getReportDate() == null) {
                result.setParseError(true);
                result.setParseErrorMessage("Report date not found in the PDF");
            }

        } catch (IOException e) {
            result.setParseError(true);
            result.setParseErrorMessage("Unable to read PDF: " + e.getMessage());
        } catch (Exception e) {
            result.setParseError(true);
            result.setParseErrorMessage("Unexpected error while reading PDF: " + e.getMessage());
        }

        return result;
    }


    private void parseEmployeeDetails(String text, ExtractedReportData result) {
        Matcher m = EMPLOYEE_ID.matcher(text);
        if (m.find()) {
            result.setEmployeeId(m.group(1).trim());
        }

        m = EMPLOYEE_NAME.matcher(text);
        if (m.find()) {
            result.setEmployeeName(m.group(1).trim());
        }

        m = DEPARTMENT.matcher(text);
        if (m.find()) {
            result.setDepartment(m.group(1).trim());
        }

        m = SHIFT_TIME.matcher(text);
        if (m.find()) {
            result.setShiftTime(m.group(1).trim());
        }

        m = REPORT_DATE.matcher(text);
        if (m.find()) {
            try {
                int day = Integer.parseInt(m.group(1));
                int month = Integer.parseInt(m.group(2));
                int year = Integer.parseInt(m.group(3));
                result.setReportDate(LocalDate.of(year, month, day)); // dd/MM/yyyy
            } catch (DateTimeException e) {
                // leave null; the caller reports it as a missing date
            }
        }
    }



    private void parseTaskTable(String text, ExtractedReportData result) {
        String table = between(text, "Task\\s*Summary", "Detailed\\s*Work\\s*Summary");
        if (table == null) {
            return;
        }

        StringBuilder block = new StringBuilder();
        LocalTime slotStart = null;
        LocalTime slotEnd = null;
        boolean inRow = false;

        for (String rawLine : table.split("\n")) {
            String line = rawLine.trim();

            Matcher start = ROW_START.matcher(line);
            if (start.matches()) {
                if (inRow) {
                    processRowBlock(slotStart, slotEnd, block.toString(), result);
                }
                slotStart = parseTime(start.group(1));
                slotEnd = parseTime(start.group(2));
                block.setLength(0);
                inRow = true;

                String rest = start.group(3).trim();
                if (!rest.isEmpty()) {
                    block.append(rest).append(' ');
                }
                continue;
            }

            if (inRow && !line.isEmpty()) {
                block.append(line).append(' ');
            }

        }

        if (inRow) {
            processRowBlock(slotStart, slotEnd, block.toString(), result);
        }
    }


    private void processRowBlock(LocalTime slotStart, LocalTime slotEnd, String blockText,
                                 ExtractedReportData result) {
        String collapsed = collapse(blockText);
        Matcher end = ROW_END.matcher(collapsed);
        if (!end.matches()) {
            return;
        }

        result.getHourlyEntries().add(new ExtractedReportData.HourlyEntry(
                slotStart, slotEnd,
                parseTime(end.group(2)), parseTime(end.group(3)),
                parseDurationMinutes(end.group(4)),
                end.group(5).trim(),
                collapse(end.group(1))));
    }



    private void parseSections(String text, ExtractedReportData result) {
        result.setDailySummary(collapseOrNull(
                between(text, "Detailed\\s*Work\\s*Summary\\s*:?", "Issues\\s*Faced")));

        result.setIssuesFaced(collapseOrNull(
                between(text, "Support\\s*Needed\\s*:?", "Next\\s*Day\\s*Plan")));

        result.setNextDayPlan(collapseOrNull(
                between(text, "Next\\s*Day\\s*Plan\\s*:?", "Self-?\\s*Evaluation")));

        result.setSelfEvaluation(collapseOrNull(
                between(text, "\\(\\s*Productivity\\s*Level[^)]*\\)\\s*:?", "Supervise")));
    }


    private void detectMeeting(ExtractedReportData result) {
        for (ExtractedReportData.HourlyEntry entry : result.getHourlyEntries()) {
            String description = entry.getDescription();
            if (description == null || entry.isBreak()) {
                continue;
            }

            String lower = description.toLowerCase();
            boolean mentionsMeeting = lower.contains("meeting");
            boolean mentionsManager = lower.contains("manager")
                    || lower.contains("ma'am") || lower.contains("ma\u2019am") || lower.contains("mam");

            if (!(mentionsMeeting && mentionsManager)) {
                continue;
            }

            ExtractedReportData.MeetingSummary meeting = new ExtractedReportData.MeetingSummary();
            meeting.setPresent(true);

            Matcher range = MEETING_TIME_RANGE.matcher(description);
            if (range.find()) {
                meeting.setMeetingTime(toOfficeTime(parseTime(range.group(1))));
                meeting.setMeetingEndTime(toOfficeTime(parseTime(range.group(2))));
                meeting.setDiscussion(description.substring(range.end()).trim());
            } else {
                meeting.setDiscussion(description);
            }

            result.setMeetingSummary(meeting);
            return;
        }
    }


    private String between(String text, String startRegex, String endRegex) {
        Matcher start = Pattern.compile(startRegex, Pattern.CASE_INSENSITIVE).matcher(text);
        if (!start.find()) {
            return null;
        }
        Matcher end = Pattern.compile(endRegex, Pattern.CASE_INSENSITIVE).matcher(text);
        if (!end.find(start.end())) {
            return null;
        }
        return text.substring(start.end(), end.start());
    }

    private LocalTime parseTime(String value) {
        try {
            String[] parts = value.trim().split(":");
            return LocalTime.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        } catch (Exception e) {
            return null;
        }
    }


    private Integer parseDurationMinutes(String token) {
        Matcher m = DURATION.matcher(token.trim());
        if (!m.matches()) {
            return null;
        }
        String hours = m.group(1);
        String minutes = m.group(2);
        if (hours == null && minutes == null) {
            return null;
        }
        int total = 0;
        if (hours != null) {
            total += Integer.parseInt(hours) * 60;
        }
        if (minutes != null) {
            total += Integer.parseInt(minutes);
        }
        return total;
    }


    private LocalTime toOfficeTime(LocalTime time) {
        if (time != null && time.getHour() >= 1 && time.getHour() <= 8) {
            return time.plusHours(12);
        }
        return time;
    }

    private String collapse(String text) {
        return text.replaceAll("\\s+", " ").trim();
    }

    private String collapseOrNull(String text) {
        if (text == null) {
            return null;
        }
        String collapsed = collapse(text);
        return collapsed.isEmpty() ? null : collapsed;
    }
}