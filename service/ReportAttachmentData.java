package com.workaudit.workaudit.service;

public record ReportAttachmentData(
        String fileName,
        String contentType,
        byte[] data) {
}
