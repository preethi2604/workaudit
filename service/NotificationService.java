package com.workaudit.workaudit.service;

import com.workaudit.workaudit.dto.NotificationResponse;
import com.workaudit.workaudit.dto.UnreadNotificationCountResponse;
import com.workaudit.workaudit.entity.DailyReport;
import com.workaudit.workaudit.entity.Employee;
import com.workaudit.workaudit.entity.EmployeeRole;
import com.workaudit.workaudit.entity.Notification;
import com.workaudit.workaudit.entity.NotificationType;
import com.workaudit.workaudit.repository.EmployeeRepository;
import com.workaudit.workaudit.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmployeeRepository employeeRepository;

    public NotificationService(
            NotificationRepository notificationRepository,
            EmployeeRepository employeeRepository
    ) {
        this.notificationRepository = notificationRepository;
        this.employeeRepository = employeeRepository;
    }

    /**
     * Creates a notification for one employee.
     */
    public Notification createNotification(
            Employee employee,
            DailyReport report,
            NotificationType type,
            String title,
            String message
    ) {

        Notification notification = new Notification(
                employee,
                report,
                type,
                title,
                message
        );

        return notificationRepository.save(notification);
    }

    /**
     * Notify all active managers that a report was submitted.
     */
    public void notifyManagersReportSubmitted(
            DailyReport report
    ) {

        List<Employee> managers =
                employeeRepository.findByRoleAndActiveTrue(
                        EmployeeRole.MANAGER
                );

        String employeeName =
                report.getEmployee().getFirstName()
                        + " "
                        + report.getEmployee().getLastName();

        String message =
                "Daily report submitted by "
                        + employeeName.trim()
                        + " for "
                        + report.getReportDate()
                        + ".";

        for (Employee manager : managers) {

            createNotification(
                    manager,
                    report,
                    NotificationType.REPORT_SUBMITTED,
                    "New Daily Report",
                    message
            );
        }
    }

    /**
     * Notify employee that report was approved.
     */
    public void notifyReportApproved(
            DailyReport report,
            String reason
    ) {

        String message =
                "Your daily report for "
                        + report.getReportDate()
                        + " has been approved.";

        if (reason != null && !reason.isBlank()) {
            message += " Reason: " + reason;
        }

        createNotification(
                report.getEmployee(),
                report,
                NotificationType.REPORT_APPROVED,
                "Report Approved",
                message
        );
    }

    /**
     * Notify employee that report was rejected.
     */
    public void notifyReportRejected(
            DailyReport report,
            String reason
    ) {

        String message =
                "Your daily report for "
                        + report.getReportDate()
                        + " has been rejected.";

        if (reason != null && !reason.isBlank()) {
            message += " Reason: " + reason;
        }

        createNotification(
                report.getEmployee(),
                report,
                NotificationType.REPORT_REJECTED,
                "Report Rejected",
                message
        );
    }

    /**
     * Notify employee when validation fails.
     */
    public void notifyValidationFailed(
            DailyReport report
    ) {

        createNotification(
                report.getEmployee(),
                report,
                NotificationType.VALIDATION_FAILED,
                "Report Validation Failed",
                "Your daily report for "
                        + report.getReportDate()
                        + " failed validation. Please review the validation errors."
        );
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(
            UUID employeeId
    ) {

        ensureEmployeeExists(employeeId);

        return notificationRepository
                .findByEmployeeIdOrderByCreatedAtDesc(employeeId)
                .stream()
                .map(NotificationResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadNotifications(
            UUID employeeId
    ) {

        ensureEmployeeExists(employeeId);

        return notificationRepository
                .findByEmployeeIdAndReadFalseOrderByCreatedAtDesc(
                        employeeId
                )
                .stream()
                .map(NotificationResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public UnreadNotificationCountResponse getUnreadCount(
            UUID employeeId
    ) {

        ensureEmployeeExists(employeeId);

        long count =
                notificationRepository
                        .countByEmployeeIdAndReadFalse(employeeId);

        return new UnreadNotificationCountResponse(count);
    }

    public NotificationResponse markAsRead(
            UUID employeeId,
            UUID notificationId
    ) {

        ensureEmployeeExists(employeeId);

        Notification notification =
                notificationRepository.findById(notificationId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Notification not found: "
                                                + notificationId
                                )
                        );

        if (!notification.getEmployee()
                .getId()
                .equals(employeeId)) {

            throw new IllegalStateException(
                    "Notification does not belong to this employee"
            );
        }

        notification.markAsRead();

        Notification saved =
                notificationRepository.save(notification);

        return NotificationResponse.fromEntity(saved);
    }

    public int markAllAsRead(
            UUID employeeId
    ) {

        ensureEmployeeExists(employeeId);

        List<Notification> notifications =
                notificationRepository
                        .findByEmployeeIdAndReadFalseOrderByCreatedAtDesc(
                                employeeId
                        );

        for (Notification notification : notifications) {
            notification.markAsRead();
        }

        notificationRepository.saveAll(notifications);

        return notifications.size();
    }

    private void ensureEmployeeExists(
            UUID employeeId
    ) {

        if (!employeeRepository.existsById(employeeId)) {

            throw new IllegalArgumentException(
                    "Employee not found: " + employeeId
            );
        }
    }
}