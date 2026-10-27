package com.workaudit.workaudit.controller;

import com.workaudit.workaudit.dto.ReportErrorResponse;
import com.workaudit.workaudit.dto.ValidationResponse;
import com.workaudit.workaudit.service.ReportValidationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
public class ValidationController {

    private final ReportValidationService validationService;

    public ValidationController(
            ReportValidationService validationService
    ) {
        this.validationService = validationService;
    }

    @PostMapping("/{reportId}/validate")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ValidationResponse> validateReport(
            @PathVariable UUID reportId
    ) {

        return ResponseEntity.ok(
                ValidationResponse.fromEntity(
                        validationService.validateReport(
                                reportId
                        )
                )
        );
    }

    @GetMapping("/{reportId}/validation")
    public ResponseEntity<ValidationResponse> getValidation(
            @PathVariable UUID reportId
    ) {

        return ResponseEntity.ok(
                ValidationResponse.fromEntity(
                        validationService.getValidation(
                                reportId
                        )
                )
        );
    }

    @GetMapping("/{reportId}/validation/errors")
    public ResponseEntity<List<ReportErrorResponse>> getErrors(
            @PathVariable UUID reportId
    ) {

        return ResponseEntity.ok(
                validationService
                        .getErrors(reportId)
                        .stream()
                        .map(ReportErrorResponse::fromEntity)
                        .toList()
        );
    }
}