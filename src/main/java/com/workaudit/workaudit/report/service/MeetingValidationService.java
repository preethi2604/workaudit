package com.workaudit.workaudit.report.service;

import com.workaudit.workaudit.report.dto.ExtractedReportData;
import org.springframework.stereotype.Service;

@Service
public class MeetingValidationService {

    private static final int MIN_DISCUSSION_LENGTH = 10;

    public static class MeetingValidationOutcome {
        private final boolean present;
        private final boolean complete;
        private final String missingFieldsSummary;

        public MeetingValidationOutcome(boolean present, boolean complete, String missingFieldsSummary) {
            this.present = present;
            this.complete = complete;
            this.missingFieldsSummary = missingFieldsSummary;
        }

        public boolean isPresent() { return present; }
        public boolean isComplete() { return complete; }
        public String getMissingFieldsSummary() { return missingFieldsSummary; }
    }

    public MeetingValidationOutcome validate(ExtractedReportData.MeetingSummary meeting) {
        if (meeting == null || !meeting.isPresent()) {
            return new MeetingValidationOutcome(false, false, "No manager meeting mentioned in the report");
        }

        StringBuilder missing = new StringBuilder();

        if (meeting.getMeetingTime() == null) {
            missing.append("meeting time; ");
        }
        if (isTooShort(meeting.getDiscussion())) {
            missing.append("what was discussed; ");
        }

        boolean complete = missing.length() == 0;
        String summary = complete ? null : "Missing: " + missing.toString().trim();

        return new MeetingValidationOutcome(true, complete, summary);
    }

    private boolean isTooShort(String text) {
        return text == null || text.trim().length() < MIN_DISCUSSION_LENGTH;
    }
}