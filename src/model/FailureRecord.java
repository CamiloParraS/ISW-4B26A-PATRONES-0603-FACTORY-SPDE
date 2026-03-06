package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FailureRecord {

    private final BatchItem item;
    private final ErrorCategory errorCategory;
    private final String errorMessage;
    private final String exceptionType; // "UnsupportedFileFormatException"
    private final LocalDateTime failedAt;

    public FailureRecord(BatchItem item, ErrorCategory errorCategory, String errorMessage,
            String exceptionType) {
        this.item = item;
        this.errorCategory = errorCategory;
        this.errorMessage = errorMessage;
        this.exceptionType = exceptionType;
        this.failedAt = LocalDateTime.now();
    }


    public BatchItem getItem() {
        return item;
    }

    public ErrorCategory getErrorCategory() {
        return errorCategory;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public LocalDateTime getFailedAt() {
        return failedAt;
    }

    public String toDisplayString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format(
                "  ✖ %s%n" + "    Category  : %s%n" + "    Exception : %s%n"
                        + "    Reason    : %s%n" + "    Failed at : %s",
                item.getLabel(), errorCategory.getLabel(), exceptionType, errorMessage,
                failedAt.format(fmt));
    }

    @Override
    public String toString() {
        return String.format("[FAILURE] %s | %s: %s", item.getLabel(), errorCategory.getLabel(),
                errorMessage);
    }
}
