package exception;

public class DocumentProcessingException extends RuntimeException {

    private final String documentName;
    private final String countryCode;

    public DocumentProcessingException(String message, String documentName, String countryCode) {
        super(message);
        this.documentName = documentName;
        this.countryCode  = countryCode;
    }

    public DocumentProcessingException(String message, String documentName, String countryCode, Throwable cause) {
        super(message, cause);
        this.documentName = documentName;
        this.countryCode  = countryCode;
    }

    public String getDocumentName() {
        return documentName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    @Override
    public String toString() {
        return String.format("[DocumentProcessingException] Country: %s | Document: %s | Reason: %s",
                countryCode, documentName, getMessage());
    }
}
