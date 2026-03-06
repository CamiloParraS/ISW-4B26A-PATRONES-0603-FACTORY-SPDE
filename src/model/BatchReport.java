package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class BatchReport {

    private final String batchId;
    private final int totalSubmitted;
    private final List<ProcessingResult> successes;
    private final List<FailureRecord> failures;
    private final LocalDateTime startedAt;
    private final LocalDateTime completedAt;
    private final long durationMs;

    public BatchReport(String batchId, int totalSubmitted, List<ProcessingResult> successes,
            List<FailureRecord> failures, LocalDateTime startedAt, LocalDateTime completedAt,
            long durationMs) {
        this.batchId = batchId;
        this.totalSubmitted = totalSubmitted;
        this.successes = Collections.unmodifiableList(new ArrayList<>(successes));
        this.failures = Collections.unmodifiableList(new ArrayList<>(failures));
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.durationMs = durationMs;
    }

    public String getBatchId() {
        return batchId;
    }

    public int getTotalSubmitted() {
        return totalSubmitted;
    }

    public List<ProcessingResult> getSuccesses() {
        return successes;
    }

    public List<FailureRecord> getFailures() {
        return failures;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public int getSuccessCount() {
        return successes.size();
    }

    public int getFailureCount() {
        return failures.size();
    }

    public boolean hasFailures() {
        return !failures.isEmpty();
    }

    public boolean allSucceeded() {
        return failures.isEmpty();
    }

    public boolean allFailed() {
        return successes.isEmpty();
    }

    public double getSuccessRate() {
        if (totalSubmitted == 0)
            return 0.0;
        return (double) successes.size() / totalSubmitted;
    }

    public String summary() {
        return String.format("%d/%d documents processed successfully (%d failed) in %dms",
                successes.size(), totalSubmitted, failures.size(), durationMs);
    }

    public String toDisplayString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        StringBuilder sb = new StringBuilder();

        sb.append("==                     BATCH PROCESSING REPORT                  ==\n");
        sb.append(String.format("  Batch ID    : %s%n", batchId));
        sb.append(String.format("  Started     : %s%n", startedAt.format(fmt)));
        sb.append(String.format("  Completed   : %s%n", completedAt.format(fmt)));
        sb.append(String.format("  Duration    : %d ms%n", durationMs));
        sb.append(String.format("  Submitted   : %d items%n", totalSubmitted));
        sb.append(String.format("  Succeeded   : %d  (%.0f%%)%n", successes.size(),
                getSuccessRate() * 100));
        sb.append(String.format("  Failed      : %d%n", failures.size()));
        sb.append("\n");

        // ── Successes ──
        if (!successes.isEmpty()) {
            sb.append("─── SUCCESSES (" + successes.size()
                    + ") ======================================\n");
            for (ProcessingResult r : successes) {
                sb.append(String.format("  ✔ %s | %s | %s%n", r.getDocumentName(),
                        r.getCountry().name(), r.getDocumentType().getDisplayName()));
                sb.append("    " + truncate(r.getMessage(), 100) + "\n");
            }
            sb.append("\n");
        }

        // ── Failures ──
        if (!failures.isEmpty()) {
            sb.append("─── FAILURES (" + failures.size()
                    + ") ======================================─\n");
            for (FailureRecord f : failures) {
                sb.append(f.toDisplayString()).append("\n");
            }
            sb.append("\n");

            sb.append("=== FAILURE BREAKDOWN BY CATEGORY ===========================\n");
            Map<ErrorCategory, Long> breakdown = failures.stream().collect(
                    Collectors.groupingBy(FailureRecord::getErrorCategory, Collectors.counting()));
            breakdown.forEach((cat, count) -> sb
                    .append(String.format("  %-24s : %d%n", cat.getLabel(), count)));
            sb.append("\n");
        }

        sb.append("  " + summary() + "\n");

        return sb.toString();
    }

    private String truncate(String s, int maxLen) {
        if (s == null)
            return "";
        return s.length() <= maxLen ? s : s.substring(0, maxLen) + "...";
    }
}
