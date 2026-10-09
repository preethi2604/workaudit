package com.workaudit.workaudit.report.service;

import com.workaudit.workaudit.report.entity.DailyReport;
import com.workaudit.workaudit.report.repository.DailyReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MissingReportService {

    private final DailyReportRepository dailyReportRepository;
    private final EmployeeDirectory employeeDirectory;

    @Autowired
    public MissingReportService(DailyReportRepository dailyReportRepository,
                                EmployeeDirectory employeeDirectory) {
        this.dailyReportRepository = dailyReportRepository;
        this.employeeDirectory = employeeDirectory;
    }

    public interface EmployeeDirectory {
        List<EmployeeInfo> findAllActiveEmployees();
    }

    public static class EmployeeInfo {
        private final String employeeId;
        private final String employeeName;
        private final String domainName;

        public EmployeeInfo(String employeeId, String employeeName, String domainName) {
            this.employeeId = employeeId;
            this.employeeName = employeeName;
            this.domainName = domainName;
        }

        public String getEmployeeId() { return employeeId; }
        public String getEmployeeName() { return employeeName; }
        public String getDomainName() { return domainName; }
    }

    public static class MissingEmployee {
        private final String employeeId;
        private final String employeeName;
        private final String domainName;

        public MissingEmployee(String employeeId, String employeeName, String domainName) {
            this.employeeId = employeeId;
            this.employeeName = employeeName;
            this.domainName = domainName;
        }

        public String getEmployeeId() { return employeeId; }
        public String getEmployeeName() { return employeeName; }
        public String getDomainName() { return domainName; }
    }

    public List<MissingEmployee> findMissingEmployees(LocalDate workingDate) {
        List<DailyReport> submitted = dailyReportRepository.findByExpectedDate(workingDate);
        Set<String> submittedIds = submitted.stream()
                .map(DailyReport::getEmployeeId)
                .collect(Collectors.toSet());

        return employeeDirectory.findAllActiveEmployees().stream()
                .filter(emp -> !submittedIds.contains(emp.getEmployeeId()))
                .map(emp -> new MissingEmployee(emp.getEmployeeId(), emp.getEmployeeName(), emp.getDomainName()))
                .collect(Collectors.toList());
    }

    public long countMissing(LocalDate workingDate) {
        return findMissingEmployees(workingDate).size();
    }
}