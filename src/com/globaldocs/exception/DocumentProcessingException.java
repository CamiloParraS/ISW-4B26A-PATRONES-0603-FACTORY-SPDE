package com.globaldocs.exception;

/**
 * Base exception for all document processing failures in the GlobalDocs system.
 * All custom exceptions extend this class to allow unified catch blocks
 * in BatchProcessor and ErrorHandler.
 */
public class DocumentProcessingException extends RuntimeException {

    private final String documentName;
    private final String countryCode;

    public DocumentProcessingException(
        String message,
        String documentName,
        String countryCode
    ) {
        super(message);
        this.documentName = documentName;
        this.countryCode = countryCode;
    }

    public DocumentProcessingException(
        String message,
        String documentName,
        String countryCode,
        Throwable cause
    ) {
        super(message, cause);
        this.documentName = documentName;
        this.countryCode = countryCode;
    }

    public String getDocumentName() {
        return documentName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    @Override
    public String toString() {
        return String.format(
            "[DocumentProcessingException] Country: %s | Document: %s | Reason: %s",
            countryCode,
            documentName,
            getMessage()
        );
    }
}
