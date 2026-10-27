package com.workaudit.workaudit.dto;

public class UnreadNotificationCountResponse {

    private long unreadCount;

    public UnreadNotificationCountResponse() {
    }

    public UnreadNotificationCountResponse(long unreadCount) {
        this.unreadCount = unreadCount;
    }

    public long getUnreadCount() {
        return unreadCount;
    }
}