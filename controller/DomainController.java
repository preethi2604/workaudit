package com.workaudit.workaudit.controller;

import com.workaudit.workaudit.dto.DomainRequest;
import com.workaudit.workaudit.dto.DomainResponse;
import com.workaudit.workaudit.service.DomainService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/domains")
public class DomainController {

    private final DomainService domainService;

    public DomainController(DomainService domainService) {
        this.domainService = domainService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DomainResponse> createDomain(
            @Valid @RequestBody DomainRequest request) {

        DomainResponse response = domainService.createDomain(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<DomainResponse>> getActiveDomains() {

        return ResponseEntity.ok(
                domainService.getActiveDomains()
        );
    }

    @GetMapping("/{domainId}")
    public ResponseEntity<DomainResponse> getDomain(
            @PathVariable UUID domainId
    ) {

        return ResponseEntity.ok(
                domainService.getDomain(domainId)
        );
    }

    @PutMapping("/{domainId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DomainResponse> updateDomain(
            @PathVariable UUID domainId,
            @Valid @RequestBody DomainRequest request
    ) {

        return ResponseEntity.ok(
                domainService.updateDomain(
                        domainId,
                        request
                )
        );
    }

    @DeleteMapping("/{domainId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateDomain(
            @PathVariable UUID domainId
    ) {

        domainService.deactivateDomain(domainId);

        return ResponseEntity.noContent().build();
    }
}