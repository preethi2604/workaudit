package com.workaudit.workaudit.report.service;

import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class StubEmployeeDirectory implements MissingReportService.EmployeeDirectory {

    @Override
    public List<MissingReportService.EmployeeInfo> findAllActiveEmployees() {
        return List.of(
                new MissingReportService.EmployeeInfo("MYHR047", "RASHMI S ANGOLKAR", "Java")
        );
    }
}
