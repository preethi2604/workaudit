package com.workaudit.workaudit.report.service;

import com.workaudit.workaudit.common.BadRequestException;
import com.workaudit.workaudit.common.ResourceNotFoundException;
import com.workaudit.workaudit.employee.entity.Employee;
import com.workaudit.workaudit.employee.repository.EmployeeRepository;
import com.workaudit.workaudit.report.dto.ReportRequest;
import com.workaudit.workaudit.report.dto.ReportResponse;
import com.workaudit.workaudit.report.entity.Report;
import com.workaudit.workaudit.report.entity.ReportStatus;
import com.workaudit.workaudit.report.repository.ReportRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final EmployeeRepository employeeRepository;

    public ReportServiceImpl(
            ReportRepository reportRepository,
            EmployeeRepository employeeRepository) {

        this.reportRepository = reportRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public ReportResponse createReport(ReportRequest request) {

        Employee employee = findEmployee(request.getEmployeeId());

        Report report = new Report();
        report.setTitle(request.getTitle().trim());
        report.setDescription(request.getDescription().trim());
        report.setReportDate(request.getReportDate());
        report.setHoursWorked(request.getHoursWorked());
        report.setEmployee(employee);
        report.setStatus(ReportStatus.SUBMITTED);

        return mapToResponse(reportRepository.save(report));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportResponse> getAllReports() {

        return reportRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ReportResponse getReportById(Long id) {

        return mapToResponse(findReport(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportResponse> getReportsByEmployee(Long employeeId) {

        findEmployee(employeeId);

        return reportRepository.findByEmployeeId(employeeId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportResponse> getReportsByDate(LocalDate date) {

        if (date == null || date.isAfter(LocalDate.now())) {
            throw new BadRequestException(
                    "Report date must be today or in the past"
            );
        }

        return reportRepository.findByReportDate(date)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportResponse> getReportsBetweenDates(
            LocalDate startDate,
            LocalDate endDate) {

        if (startDate == null || endDate == null) {
            throw new BadRequestException(
                    "Start date and end date are required"
            );
        }

        if (startDate.isAfter(endDate)) {
            throw new BadRequestException(
                    "Start date cannot be after end date"
            );
        }

        if (endDate.isAfter(LocalDate.now())) {
            throw new BadRequestException(
                    "End date cannot be in the future"
            );
        }

        return reportRepository
                .findByReportDateBetween(startDate, endDate)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ReportResponse updateReport(
            Long id,
            ReportRequest request) {

        Report report = findReport(id);
        Employee employee = findEmployee(request.getEmployeeId());

        report.setTitle(request.getTitle().trim());
        report.setDescription(request.getDescription().trim());
        report.setReportDate(request.getReportDate());
        report.setHoursWorked(request.getHoursWorked());
        report.setEmployee(employee);

        // Preserve the existing status during normal updates.
        return mapToResponse(reportRepository.save(report));
    }

    @Override
    public ReportResponse updateReportStatus(Long id, String status) {

        Report report = findReport(id);

        try {
            ReportStatus reportStatus = ReportStatus.valueOf(
                    status.trim().toUpperCase(Locale.ROOT)
            );

            report.setStatus(reportStatus);

        } catch (IllegalArgumentException ex) {
            throw new BadRequestException(
                    "Invalid report status. Allowed values: SUBMITTED, REVIEWED, REJECTED"
            );
        }

        return mapToResponse(reportRepository.save(report));
    }

    @Override
    public void deleteReport(Long id) {

        Report report = findReport(id);
        reportRepository.delete(report);
    }

    private Report findReport(Long id) {

        return reportRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Report not found with ID: " + id
                        )
                );
    }

    private Employee findEmployee(Long employeeId) {

        return employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee not found with ID: " + employeeId
                        )
                );
    }

    private ReportResponse mapToResponse(Report report) {

        Employee employee = report.getEmployee();

        String employeeName =
                employee.getFirstName() + " " + employee.getLastName();

        return new ReportResponse(
                report.getId(),
                report.getTitle(),
                report.getDescription(),
                report.getReportDate(),
                report.getHoursWorked(),
                report.getStatus(),
                employee.getId(),
                employee.getEmployeeCode(),
                employeeName,
                report.getCreatedAt(),
                report.getUpdatedAt()
        );
    }
}