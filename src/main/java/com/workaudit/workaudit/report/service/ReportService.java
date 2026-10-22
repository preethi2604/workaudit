package com.workaudit.workaudit.report.service;

import com.workaudit.workaudit.report.dto.ReportRequest;
import com.workaudit.workaudit.report.dto.ReportResponse;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {

    ReportResponse createReport(ReportRequest request);

    List<ReportResponse> getAllReports();

    ReportResponse getReportById(Long id);

    List<ReportResponse> getReportsByEmployee(Long employeeId);

    List<ReportResponse> getReportsByDate(LocalDate date);

    List<ReportResponse> getReportsBetweenDates(
            LocalDate startDate,
            LocalDate endDate
    );

    ReportResponse updateReport(Long id, ReportRequest request);

    ReportResponse updateReportStatus(Long id, String status);

    void deleteReport(Long id);
}