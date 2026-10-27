package com.workaudit.workaudit.controller;

import com.workaudit.workaudit.dto.NotificationResponse;
import com.workaudit.workaudit.dto.UnreadNotificationCountResponse;
import com.workaudit.workaudit.entity.Employee;
import com.workaudit.workaudit.exception.ResourceNotFoundException;
import com.workaudit.workaudit.repository.EmployeeRepository;
import com.workaudit.workaudit.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@PreAuthorize("isAuthenticated()")
public class NotificationController {

    private final NotificationService notificationService;
    private final EmployeeRepository employeeRepository;

    public NotificationController(
            NotificationService notificationService,
            EmployeeRepository employeeRepository) {
        this.notificationService = notificationService;
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<NotificationResponse>> getNotifications(
            @PathVariable UUID employeeId,
            Authentication authentication) {
        authorizeEmployeeScope(employeeId, authentication);
        return ResponseEntity.ok(notificationService.getNotifications(employeeId));
    }

    @GetMapping("/employee/{employeeId}/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(
            @PathVariable UUID employeeId,
            Authentication authentication) {
        authorizeEmployeeScope(employeeId, authentication);
        return ResponseEntity.ok(notificationService.getUnreadNotifications(employeeId));
    }

    @GetMapping("/employee/{employeeId}/unread/count")
    public ResponseEntity<UnreadNotificationCountResponse> getUnreadCount(
            @PathVariable UUID employeeId,
            Authentication authentication) {
        authorizeEmployeeScope(employeeId, authentication);
        return ResponseEntity.ok(notificationService.getUnreadCount(employeeId));
    }

    @PutMapping("/employee/{employeeId}/{notificationId}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable UUID employeeId,
            @PathVariable UUID notificationId,
            Authentication authentication) {
        authorizeEmployeeScope(employeeId, authentication);
        return ResponseEntity.ok(
                notificationService.markAsRead(employeeId, notificationId));
    }

    @PutMapping("/employee/{employeeId}/read-all")
    public ResponseEntity<Integer> markAllAsRead(
            @PathVariable UUID employeeId,
            Authentication authentication) {
        authorizeEmployeeScope(employeeId, authentication);
        return ResponseEntity.ok(notificationService.markAllAsRead(employeeId));
    }

    /**
     * Employees and managers may access only their own notifications.
     * Administrators may access notifications for troubleshooting/support.
     */
    private void authorizeEmployeeScope(
            UUID requestedEmployeeId,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authentication is required");
        }

        boolean administrator = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
        if (administrator) {
            return;
        }

        Employee currentEmployee = employeeRepository
                .findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Authenticated employee was not found"));

        if (!currentEmployee.isActive()) {
            throw new AccessDeniedException(
                    "Inactive employees cannot access notifications");
        }

        if (!currentEmployee.getId().equals(requestedEmployeeId)) {
            throw new AccessDeniedException(
                    "You can only access your own notifications");
        }
    }
}
