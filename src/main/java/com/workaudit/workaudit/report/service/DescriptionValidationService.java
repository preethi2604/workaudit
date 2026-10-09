package com.workaudit.workaudit.report.service;

import com.workaudit.workaudit.report.dto.ExtractedReportData;
import com.workaudit.workaudit.report.entity.DescriptionLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
public class DescriptionValidationService {

    private static final List<String> GENERIC_PHRASES = Arrays.asList(
            "worked", "work", "development", "testing", "meeting", "coding",
            "project work", "done", "continued work", "continue work",
            "task", "fixing", "review", "n/a", "na", "nil", "-"
    );

    private static final int MIN_WORDS = 3;

    private final ValidationConfigService configService;

    @Autowired
    public DescriptionValidationService(ValidationConfigService configService) {
        this.configService = configService;
    }

    public DescriptionLevel validateEntry(String description) {
        if (description == null || description.trim().isEmpty()) {
            return DescriptionLevel.RED;
        }

        String normalized = normalize(description);
        int minLength = configService.getPoorDescriptionMinLength();

        if (normalized.length() < minLength
                || wordCount(normalized) < MIN_WORDS
                || isGeneric(normalized)) {
            return DescriptionLevel.YELLOW;
        }

        return DescriptionLevel.GREEN;
    }

    public DescriptionLevel validateDailySummary(String dailySummary) {
        if (dailySummary == null || dailySummary.trim().isEmpty()) {
            return DescriptionLevel.RED;
        }
        return validateEntry(dailySummary);
    }

    public DescriptionLevel validateOverall(ExtractedReportData extracted) {
        DescriptionLevel worst = validateDailySummary(extracted.getDailySummary());

        boolean anyWorkRow = false;
        for (ExtractedReportData.HourlyEntry entry : extracted.getHourlyEntries()) {
            if (entry.isBreak()) {
                continue;
            }
            anyWorkRow = true;
            worst = worse(worst, validateEntry(entry.getDescription()));
        }

        if (!anyWorkRow) {
            worst = DescriptionLevel.RED;
        }

        return worst;
    }

    private String normalize(String text) {
        return text.trim().toLowerCase(Locale.ROOT).replaceAll("[.!,;:]+$", "");
    }

    private int wordCount(String normalized) {
        return normalized.isEmpty() ? 0 : normalized.split("\\s+").length;
    }

    private boolean isGeneric(String normalized) {
        return GENERIC_PHRASES.contains(normalized);
    }

    private DescriptionLevel worse(DescriptionLevel a, DescriptionLevel b) {
        if (a == DescriptionLevel.RED || b == DescriptionLevel.RED) {
            return DescriptionLevel.RED;
        }
        if (a == DescriptionLevel.YELLOW || b == DescriptionLevel.YELLOW) {
            return DescriptionLevel.YELLOW;
        }
        return DescriptionLevel.GREEN;
    }
}