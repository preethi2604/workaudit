package com.workaudit.workaudit.report.service;

import com.workaudit.workaudit.report.dto.ExtractedReportData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;

@Service
public class WorkingHoursService {

    private final ValidationConfigService configService;

    @Autowired
    public WorkingHoursService(ValidationConfigService configService) {
        this.configService = configService;
    }

    public enum HoursCategory {
        FULL_DAY, ONE_HOUR_SHORT, TWO_HOURS_SHORT, HALF_DAY, BELOW_HALF_DAY, NO_HOURS
    }

    public static class HoursResult {
        private final double totalHours;
        private final HoursCategory category;
        private final boolean meetsRequirement;

        public HoursResult(double totalHours, HoursCategory category, boolean meetsRequirement) {
            this.totalHours = totalHours;
            this.category = category;
            this.meetsRequirement = meetsRequirement;
        }

        public double getTotalHours() { return totalHours; }
        public HoursCategory getCategory() { return category; }
        public boolean isMeetsRequirement() { return meetsRequirement; }
    }


    public double calculateTotalHours(ExtractedReportData extracted) {
        LocalTime lunchStart = configService.getLunchStart();
        LocalTime lunchEnd = configService.getLunchEnd();

        double workMinutes = 0;
        boolean anyWork = false;

        for (ExtractedReportData.HourlyEntry entry : extracted.getHourlyEntries()) {
            if (entry.isBreak()) {
                continue;
            }
            if (entry.getStartTime() == null || entry.getEndTime() == null) {
                continue;
            }

            LocalTime start = toOfficeTime(entry.getStartTime());
            LocalTime end = toOfficeTime(entry.getEndTime());

            if (!end.isAfter(start)) {
                continue;
            }

            long minutes = Duration.between(start, end).toMinutes();
            minutes -= overlapMinutes(start, end, lunchStart, lunchEnd);

            workMinutes += minutes;
            anyWork = true;
        }

        if (!anyWork) {
            return 0.0;
        }

        long lunchMinutes = Duration.between(lunchStart, lunchEnd).toMinutes();
        return (workMinutes + lunchMinutes) / 60.0;
    }


    public LocalTime getLoginTime(ExtractedReportData extracted) {
        LocalTime earliest = null;
        for (ExtractedReportData.HourlyEntry entry : extracted.getHourlyEntries()) {
            if (entry.isBreak() || entry.getStartTime() == null) {
                continue;
            }
            LocalTime start = toOfficeTime(entry.getStartTime());
            if (earliest == null || start.isBefore(earliest)) {
                earliest = start;
            }
        }
        return earliest;
    }


    public boolean isLateLogin(ExtractedReportData extracted) {
        LocalTime login = getLoginTime(extracted);
        return login != null && login.isAfter(configService.getOfficeStart());
    }


    private LocalTime toOfficeTime(LocalTime time) {
        int hour = time.getHour();
        if (hour >= 1 && hour <= 8) {
            return time.plusHours(12);
        }
        return time;
    }

    private long overlapMinutes(LocalTime start, LocalTime end, LocalTime blockStart, LocalTime blockEnd) {
        LocalTime overlapStart = start.isAfter(blockStart) ? start : blockStart;
        LocalTime overlapEnd = end.isBefore(blockEnd) ? end : blockEnd;

        if (!overlapEnd.isAfter(overlapStart)) {
            return 0;
        }
        return Duration.between(overlapStart, overlapEnd).toMinutes();
    }

    public HoursResult classify(double totalHours) {
        double required = configService.getRequiredHours();
        double halfDay = configService.getHalfDayHours();

        HoursCategory category;
        if (totalHours <= 0) {
            category = HoursCategory.NO_HOURS;
        } else if (totalHours >= required) {
            category = HoursCategory.FULL_DAY;
        } else if (totalHours >= required - 1) {
            category = HoursCategory.ONE_HOUR_SHORT;
        } else if (totalHours >= required - 2) {
            category = HoursCategory.TWO_HOURS_SHORT;
        } else if (totalHours >= halfDay) {
            category = HoursCategory.HALF_DAY;
        } else {
            category = HoursCategory.BELOW_HALF_DAY;
        }

        boolean meetsRequirement = totalHours >= required;
        return new HoursResult(totalHours, category, meetsRequirement);
    }
}