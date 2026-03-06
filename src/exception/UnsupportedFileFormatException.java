package exception;

import java.util.List;
import java.util.stream.Collectors;
import model.CountryCode;
import model.DocumentType;
import model.FileFormat;

public class UnsupportedFileFormatException extends DocumentProcessingException {

    private final FileFormat attemptedFormat;
    private final List<FileFormat> allowedFormats;

    public UnsupportedFileFormatException(CountryCode country, DocumentType documentType,
            FileFormat attemptedFormat, List<FileFormat> allowedFormats, String documentName) {

        super(buildMessage(country, documentType, attemptedFormat, allowedFormats), documentName,
                country.name());
        this.attemptedFormat = attemptedFormat;
        this.allowedFormats = allowedFormats;
    }

    public FileFormat getAttemptedFormat() {
        return attemptedFormat;
    }

    public List<FileFormat> getAllowedFormats() {
        return allowedFormats;
    }

    private static String buildMessage(CountryCode country, DocumentType documentType,
            FileFormat attemptedFormat, List<FileFormat> allowedFormats) {

        String allowed = allowedFormats.stream().map(FileFormat::getExtension)
                .collect(Collectors.joining(", "));

        return String.format("Format '%s' is not supported for '%s' in %s. Allowed formats: [%s]",
                attemptedFormat.getExtension(), documentType.getDisplayName(), country.name(),
                allowed);
    }
}
