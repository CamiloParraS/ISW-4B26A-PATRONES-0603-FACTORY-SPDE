package batch;

import exception.DocumentProcessingException;
import exception.InvalidDocumentForCountryException;
import exception.UnsupportedFileFormatException;
import model.BatchItem;
import model.ErrorCategory;
import model.FailureRecord;

public class ErrorHandler {
    private ErrorHandler() { /* utility class — no instantiation */ }

    public static FailureRecord handle(BatchItem item, Throwable ex) {
        ErrorCategory category = classify(ex);
        String message = buildMessage(item, ex, category);
        String exType = ex.getClass().getSimpleName();

        return new FailureRecord(item, category, message, exType);
    }

    public static ErrorCategory classify(Throwable ex) {
        if (ex instanceof InvalidDocumentForCountryException) {
            return ErrorCategory.COUNTRY_RULE;
        }
        if (ex instanceof UnsupportedFileFormatException) {
            return ErrorCategory.FORMAT;
        }
        if (ex instanceof DocumentProcessingException dpe) {
            // Distinguish validation (setup-time) from processing (runtime) failures
            String msg = dpe.getMessage() != null ? dpe.getMessage().toLowerCase() : "";
            if (msg.contains("null") || msg.contains("blank") || msg.contains("cannot determine")) {
                return ErrorCategory.VALIDATION;
            }
            return ErrorCategory.PROCESSING;
        }
        if (ex instanceof IllegalArgumentException) {
            return ErrorCategory.VALIDATION;
        }
        return ErrorCategory.SYSTEM;
    }


    private static String buildMessage(BatchItem item, Throwable ex, ErrorCategory category) {
        String baseMsg = ex.getMessage() != null ? ex.getMessage() : "No details available.";

        return switch (category) {

            case COUNTRY_RULE -> String.format(
                    "Country rule violation for %s in %s. %s "
                            + "Please review the country's allowed document types before retrying.",
                    item.getDocumentType().getDisplayName(), item.getCountry().getFullName(),
                    baseMsg);

            case FORMAT -> String.format("File format '%s' is not accepted for '%s' in %s. %s",
                    item.getFileFormat().getExtension(), item.getDocumentType().getDisplayName(),
                    item.getCountry().name(), baseMsg);

            case VALIDATION -> String.format("Input validation failed for item '%s'. %s "
                    + "Check that the file is not null, the filename is valid, and the format is recognizable.",
                    item.getFile().getName(), baseMsg);

            case PROCESSING -> String.format("Processing logic failed for '%s' (%s / %s). %s",
                    item.getFile().getName(), item.getCountry().name(),
                    item.getDocumentType().getDisplayName(), baseMsg);

            case SYSTEM -> String.format(
                    "Unexpected system error while processing '%s'. Exception: %s — %s. "
                            + "This may indicate an I/O issue or an unhandled edge case.",
                    item.getFile().getName(), ex.getClass().getSimpleName(), baseMsg);
        };
    }

    public static String formatInline(BatchItem item, Throwable ex) {
        ErrorCategory category = classify(ex);
        return String.format("[%s] %s | %s: %s", category.name(), item.getLabel(),
                ex.getClass().getSimpleName(), ex.getMessage());
    }
}
