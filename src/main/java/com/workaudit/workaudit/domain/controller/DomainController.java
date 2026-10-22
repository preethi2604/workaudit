package com.workaudit.workaudit.domain.controller;

import com.workaudit.workaudit.domain.dto.DomainRequest;
import com.workaudit.workaudit.domain.dto.DomainResponse;
import com.workaudit.workaudit.domain.service.DomainService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/domains")
public class DomainController {

    private final DomainService domainService;

    public DomainController(DomainService domainService) {
        this.domainService = domainService;
    }

    @PostMapping
    public ResponseEntity<DomainResponse> createDomain(
            @Valid @RequestBody DomainRequest request) {

        DomainResponse response = domainService.createDomain(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<DomainResponse>> getAllDomains() {

        return ResponseEntity.ok(
                domainService.getAllDomains()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<DomainResponse> getDomainById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                domainService.getDomainById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<DomainResponse> updateDomain(
            @PathVariable Long id,
            @Valid @RequestBody DomainRequest request) {

        return ResponseEntity.ok(
                domainService.updateDomain(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDomain(
            @PathVariable Long id) {

        domainService.deleteDomain(id);

        return ResponseEntity.noContent().build();
    }
}