package com.workaudit.workaudit.report.service;

import com.workaudit.workaudit.report.entity.ValidationConfig;
import com.workaudit.workaudit.report.repository.ValidationConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class ValidationConfigService {

    public static final String REQUIRED_HOURS = "required.hours";
    public static final String HALFDAY_HOURS = "halfday.hours";
    public static final String DUPLICATE_SIMILARITY_THRESHOLD = "duplicate.similarity.threshold";
    public static final String POOR_DESCRIPTION_MIN_LENGTH = "poor.description.min.length";
    public static final String REPEATED_DAYS_THRESHOLD = "repeated.description.days.threshold";
    public static final String OFFICE_START = "office.start";
    public static final String OFFICE_END = "office.end";
    public static final String LUNCH_START = "lunch.start";
    public static final String LUNCH_END = "lunch.end";

    private final ValidationConfigRepository configRepository;

    @Autowired
    public ValidationConfigService(ValidationConfigRepository configRepository) {
        this.configRepository = configRepository;
    }


    public double getRequiredHours() {
        return getDouble(REQUIRED_HOURS, 9.0);
    }

    public double getHalfDayHours() {
        return getDouble(HALFDAY_HOURS, 4.0);
    }


    public double getDuplicateSimilarityThreshold() {
        return getDouble(DUPLICATE_SIMILARITY_THRESHOLD, 0.85);
    }

    public int getPoorDescriptionMinLength() {
        return (int) getDouble(POOR_DESCRIPTION_MIN_LENGTH, 15);
    }


    public int getRepeatedDaysThreshold() {
        return (int) getDouble(REPEATED_DAYS_THRESHOLD, 3);
    }

    public LocalTime getOfficeStart() {
        return getTime(OFFICE_START, LocalTime.of(9, 30));
    }

    public LocalTime getOfficeEnd() {
        return getTime(OFFICE_END, LocalTime.of(18, 30));
    }

    public LocalTime getLunchStart() {
        return getTime(LUNCH_START, LocalTime.of(13, 0));
    }

    public LocalTime getLunchEnd() {
        return getTime(LUNCH_END, LocalTime.of(14, 0));
    }

    private double getDouble(String key, double defaultValue) {
        return configRepository.findByConfigKey(key)
                .map(ValidationConfig::getConfigValue)
                .map(this::safeParseDouble)
                .orElse(defaultValue);
    }

    private Double safeParseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private LocalTime getTime(String key, LocalTime defaultValue) {
        return configRepository.findByConfigKey(key)
                .map(ValidationConfig::getConfigValue)
                .map(this::safeParseTime)
                .orElse(defaultValue);
    }

    private LocalTime safeParseTime(String value) {
        try {
            return LocalTime.parse(value.trim());
        } catch (Exception e) {
            return null;
        }
    }
}