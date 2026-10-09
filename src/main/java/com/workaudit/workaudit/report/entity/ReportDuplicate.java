package com.workaudit.workaudit.report.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "report_duplicates")
public class ReportDuplicate {

    public enum MatchType {
        EXACT_DUPLICATE,
        HIGHLY_SIMILAR,
        REPEATED_SELF
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private DailyReport report;

    @Column(name = "matched_report_id")
    private Long matchedReportId;

    @Column(name = "matched_employee_id", length = 50)
    private String matchedEmployeeId;

    @Column(name = "similarity_score", nullable = false)
    private double similarityScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "match_type", nullable = false, length = 30)
    private MatchType matchType;

    @Column(name = "detected_at", nullable = false)
    private LocalDateTime detectedAt;

    protected ReportDuplicate() {
        // JPA
    }

    public ReportDuplicate(DailyReport report, Long matchedReportId, String matchedEmployeeId,
                           double similarityScore, MatchType matchType) {
        this.report = report;
        this.matchedReportId = matchedReportId;
        this.matchedEmployeeId = matchedEmployeeId;
        this.similarityScore = similarityScore;
        this.matchType = matchType;
        this.detectedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public DailyReport getReport() { return report; }
    public Long getMatchedReportId() { return matchedReportId; }
    public String getMatchedEmployeeId() { return matchedEmployeeId; }
    public double getSimilarityScore() { return similarityScore; }
    public MatchType getMatchType() { return matchType; }
    public LocalDateTime getDetectedAt() { return detectedAt; }
}