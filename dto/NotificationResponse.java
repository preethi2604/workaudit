package com.workaudit.workaudit.dto;

import com.workaudit.workaudit.entity.Notification;
import com.workaudit.workaudit.entity.NotificationType;

import java.time.Instant;
import java.util.UUID;

public class NotificationResponse {

    private UUID id;
    private UUID employeeId;
    private UUID reportId;

    private NotificationType type;

    private String title;
    private String message;

    private boolean read;

    private Instant createdAt;
    private Instant readAt;

    public NotificationResponse() {
    }

    public NotificationResponse(
            UUID id,
            UUID employeeId,
            UUID reportId,
            NotificationType type,
            String title,
            String message,
            boolean read,
            Instant createdAt,
            Instant readAt
    ) {
        this.id = id;
        this.employeeId = employeeId;
        this.reportId = reportId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.read = read;
        this.createdAt = createdAt;
        this.readAt = readAt;
    }

    public static NotificationResponse fromEntity(
            Notification notification
    ) {

        return new NotificationResponse(
                notification.getId(),
                notification.getEmployee().getId(),
                notification.getReport() != null
                        ? notification.getReport().getId()
                        : null,
                notification.getType(),
                notification.getTitle(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedAt(),
                notification.getReadAt()
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public UUID getReportId() {
        return reportId;
    }

    public NotificationType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public boolean isRead() {
        return read;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getReadAt() {
        return readAt;
    }
}