package exception;

import model.CountryCode;
import model.DocumentType;

public class InvalidDocumentForCountryException extends DocumentProcessingException {

    private final CountryCode country;
    private final DocumentType documentType;

    public InvalidDocumentForCountryException(CountryCode country, DocumentType documentType,
            String documentName) {
        super(buildMessage(country, documentType), documentName, country.name());
        this.country = country;
        this.documentType = documentType;
    }

    public CountryCode getCountry() {
        return country;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    private static String buildMessage(CountryCode country, DocumentType documentType) {
        return String.format(
                "Document type '%s' is not permitted in %s (%s). "
                        + "Please check the country-specific processing rules.",
                documentType.getDisplayName(), country.getFullName(), country.name());
    }
}
