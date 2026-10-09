package com.workaudit.workaudit.report.service;

import com.workaudit.workaudit.report.dto.DuplicateMatch;
import org.apache.commons.text.similarity.CosineDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class DuplicateDetectionService {

    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9\\s]");
    private static final Pattern MULTI_SPACE = Pattern.compile("\\s+");

    private final ValidationConfigService configService;
    private final CosineDistance cosineDistance = new CosineDistance();

    @Autowired
    public DuplicateDetectionService(ValidationConfigService configService) {
        this.configService = configService;
    }

    public static class ComparisonCandidate {
        private final Long reportId;
        private final String employeeId;
        private final LocalDate reportDate;
        private final String combinedText;

        public ComparisonCandidate(Long reportId, String employeeId,
                                   LocalDate reportDate, String combinedText) {
            this.reportId = reportId;
            this.employeeId = employeeId;
            this.reportDate = reportDate;
            this.combinedText = combinedText;
        }

        public Long getReportId() { return reportId; }
        public String getEmployeeId() { return employeeId; }
        public LocalDate getReportDate() { return reportDate; }
        public String getCombinedText() { return combinedText; }
    }

    public List<DuplicateMatch> checkAgainstPeers(String targetEmployeeId,
                                                  String targetText,
                                                  List<ComparisonCandidate> sameDayPeers) {
        List<DuplicateMatch> matches = new ArrayList<>();
        String normalizedTarget = normalize(targetText);

        if (normalizedTarget.isBlank()) {
            return matches;
        }

        double threshold = configService.getDuplicateSimilarityThreshold();

        for (ComparisonCandidate peer : sameDayPeers) {
            if (peer.getEmployeeId().equals(targetEmployeeId)) {
                continue;
            }

            String normalizedPeer = normalize(peer.getCombinedText());
            if (normalizedPeer.isBlank()) {
                continue;
            }

            if (normalizedTarget.equals(normalizedPeer)) {
                matches.add(new DuplicateMatch(peer.getReportId(), peer.getEmployeeId(),
                        1.0, DuplicateMatch.MatchType.EXACT_DUPLICATE));
                continue;
            }

            double score = similarity(normalizedTarget, normalizedPeer);
            if (score >= threshold) {
                matches.add(new DuplicateMatch(peer.getReportId(), peer.getEmployeeId(),
                        score, DuplicateMatch.MatchType.HIGHLY_SIMILAR));
            }
        }

        return matches;
    }

    public List<DuplicateMatch> checkAgainstOwnHistory(String targetText,
                                                       List<ComparisonCandidate> employeeHistory) {
        List<DuplicateMatch> matches = new ArrayList<>();
        String normalizedTarget = normalize(targetText);

        if (normalizedTarget.isBlank()) {
            return matches;
        }

        double threshold = configService.getDuplicateSimilarityThreshold();
        int requiredStreak = configService.getRepeatedDaysThreshold();

        for (ComparisonCandidate previous : employeeHistory) {
            String normalizedPrevious = normalize(previous.getCombinedText());
            if (normalizedPrevious.isBlank()) {
                break;
            }

            double score = normalizedTarget.equals(normalizedPrevious)
                    ? 1.0
                    : similarity(normalizedTarget, normalizedPrevious);

            if (score >= threshold) {
                matches.add(new DuplicateMatch(previous.getReportId(), previous.getEmployeeId(),
                        score, DuplicateMatch.MatchType.REPEATED_SELF));
            } else {
                break;
            }
        }

        if (matches.size() < requiredStreak) {
            return new ArrayList<>();
        }
        return matches;
    }

    public String sha256(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(normalize(text).getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }

    private double similarity(String a, String b) {
        return 1.0 - cosineDistance.apply(a, b);
    }

    private String normalize(String text) {
        if (text == null) {
            return "";
        }
        String lower = text.toLowerCase(Locale.ROOT);
        String noPunctuation = NON_ALPHANUMERIC.matcher(lower).replaceAll(" ");
        return MULTI_SPACE.matcher(noPunctuation).replaceAll(" ").trim();
    }
}