package com.workaudit.workaudit.manager.controller;

import com.workaudit.workaudit.manager.dto.ManagerRequest;
import com.workaudit.workaudit.manager.dto.ManagerResponse;
import com.workaudit.workaudit.manager.service.ManagerService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/managers")
public class ManagerController {

    private final ManagerService managerService;

    public ManagerController(ManagerService managerService) {
        this.managerService = managerService;
    }

    @PostMapping
    public ResponseEntity<ManagerResponse> createManager(
            @Valid @RequestBody ManagerRequest request) {

        ManagerResponse response =
                managerService.createManager(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ManagerResponse>> getAllManagers() {

        return ResponseEntity.ok(
                managerService.getAllManagers()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ManagerResponse> getManagerById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                managerService.getManagerById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ManagerResponse> updateManager(
            @PathVariable Long id,
            @Valid @RequestBody ManagerRequest request) {

        return ResponseEntity.ok(
                managerService.updateManager(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteManager(
            @PathVariable Long id) {

        managerService.deleteManager(id);

        return ResponseEntity.noContent().build();
    }
}