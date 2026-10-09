package com.workaudit.workaudit.report.entity;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "manager_meetings")
public class ManagerMeeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false, unique = true)
    private DailyReport report;

    @Column(name = "meeting_time")
    private LocalTime meetingTime;

    @Column(name = "manager_updates", columnDefinition = "TEXT")
    private String managerUpdates;

    @Column(name = "employee_updates", columnDefinition = "TEXT")
    private String employeeUpdates;

    @Column(name = "action_items", columnDefinition = "TEXT")
    private String actionItems;


    @Column(name = "was_present", nullable = false)
    private boolean wasPresent = false;

    protected ManagerMeeting() {
        // JPA
    }

    public ManagerMeeting(DailyReport report) {
        this.report = report;
    }

    public Long getId() { return id; }

    public DailyReport getReport() { return report; }
    public void setReport(DailyReport report) { this.report = report; }

    public LocalTime getMeetingTime() { return meetingTime; }
    public void setMeetingTime(LocalTime meetingTime) { this.meetingTime = meetingTime; }

    public String getManagerUpdates() { return managerUpdates; }
    public void setManagerUpdates(String managerUpdates) { this.managerUpdates = managerUpdates; }

    public String getEmployeeUpdates() { return employeeUpdates; }
    public void setEmployeeUpdates(String employeeUpdates) { this.employeeUpdates = employeeUpdates; }

    public String getActionItems() { return actionItems; }
    public void setActionItems(String actionItems) { this.actionItems = actionItems; }

    public boolean isWasPresent() { return wasPresent; }
    public void setWasPresent(boolean wasPresent) { this.wasPresent = wasPresent; }
}