package com.globaldocs.model;

/**
 * Enum representing the outcome of a document processing attempt.
 * Used in ProcessingResult and BatchReport to indicate per-document status.
 */
public enum ProcessStatus {

    SUCCESS("Processed successfully"),
    FAILED("Processing failed"),
    SKIPPED("Skipped — validation not passed");

    private final String description;

    ProcessStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name() + ": " + description;
    }
}
