package model;

public enum ProcessStatus {

    SUCCESS("Processed successfully"), FAILED("Processing failed"), SKIPPED(
            "Skipped — validation not passed");

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
