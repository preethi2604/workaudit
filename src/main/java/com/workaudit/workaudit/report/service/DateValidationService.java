package com.workaudit.workaudit.report.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class DateValidationService {

    public enum DateCheckResult {
        MATCHES,
        FUTURE_DATED,
        BACK_DATED
    }


    public DateCheckResult checkReportDate(LocalDate reportDate, LocalDate expectedDate) {
        if (reportDate == null || expectedDate == null) {
            return DateCheckResult.BACK_DATED;
        }
        if (reportDate.isEqual(expectedDate)) {
            return DateCheckResult.MATCHES;
        }
        return reportDate.isAfter(expectedDate)
                ? DateCheckResult.FUTURE_DATED
                : DateCheckResult.BACK_DATED;
    }


    public boolean isLate(LocalDate reportDate, LocalDateTime submittedAt) {
        if (reportDate == null || submittedAt == null) {
            return false;
        }
        return submittedAt.toLocalDate().isAfter(reportDate);
    }
}