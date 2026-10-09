package com.workaudit.workaudit.report.dto;

public class DuplicateMatch {

    public enum MatchType {
        EXACT_DUPLICATE,
        HIGHLY_SIMILAR,
        REPEATED_SELF
    }

    private final Long comparedReportId;
    private final String comparedEmployeeId;
    private final double similarityScore;
    private final MatchType matchType;

    public DuplicateMatch(Long comparedReportId, String comparedEmployeeId,
                          double similarityScore, MatchType matchType) {
        this.comparedReportId = comparedReportId;
        this.comparedEmployeeId = comparedEmployeeId;
        this.similarityScore = similarityScore;
        this.matchType = matchType;
    }

    public Long getComparedReportId() { return comparedReportId; }
    public String getComparedEmployeeId() { return comparedEmployeeId; }
    public double getSimilarityScore() { return similarityScore; }
    public MatchType getMatchType() { return matchType; }

    public int getSimilarityPercentage() {
        return (int) Math.round(similarityScore * 100);
    }
}