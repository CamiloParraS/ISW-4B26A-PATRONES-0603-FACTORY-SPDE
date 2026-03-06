package model;

public enum ErrorCategory {

    VALIDATION("Validation Error",
            "The document failed basic validation checks (null file, missing fields, etc.)"), FORMAT(
                    "Unsupported Format",
                    "The file format is not supported for the given document type or country."), COUNTRY_RULE(
                            "Country Rule Violation",
                            "The document type or format combination is not permitted for the target country."), PROCESSING(
                                    "Processing Failure",
                                    "The processor encountered an error while handling the document content."), SYSTEM(
                                            "System Error",
                                            "An unexpected I/O or runtime error occurred during processing.");

    private final String label;
    private final String description;

    ErrorCategory(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return label;
    }
}
