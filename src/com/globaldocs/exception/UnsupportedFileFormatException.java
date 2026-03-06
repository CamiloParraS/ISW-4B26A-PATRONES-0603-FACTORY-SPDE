package com.globaldocs.exception;

import com.globaldocs.model.CountryCode;
import com.globaldocs.model.DocumentType;
import com.globaldocs.model.FileFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Thrown when a file format is not supported for a given country + document type combination.
 * Each country factory defines which formats are acceptable per document type.
 *
 * Example: Colombia does not allow Digital Certificates in .xlsx format.
 */
public class UnsupportedFileFormatException
    extends DocumentProcessingException
{

    private final FileFormat attemptedFormat;
    private final List<FileFormat> allowedFormats;

    public UnsupportedFileFormatException(
        CountryCode country,
        DocumentType documentType,
        FileFormat attemptedFormat,
        List<FileFormat> allowedFormats,
        String documentName
    ) {
        super(
            buildMessage(
                country,
                documentType,
                attemptedFormat,
                allowedFormats
            ),
            documentName,
            country.name()
        );
        this.attemptedFormat = attemptedFormat;
        this.allowedFormats = allowedFormats;
    }

    public FileFormat getAttemptedFormat() {
        return attemptedFormat;
    }

    public List<FileFormat> getAllowedFormats() {
        return allowedFormats;
    }

    private static String buildMessage(
        CountryCode country,
        DocumentType documentType,
        FileFormat attemptedFormat,
        List<FileFormat> allowedFormats
    ) {
        String allowed = allowedFormats
            .stream()
            .map(FileFormat::getExtension)
            .collect(Collectors.joining(", "));

        return String.format(
            "Format '%s' is not supported for '%s' in %s. Allowed formats: [%s]",
            attemptedFormat.getExtension(),
            documentType.getDisplayName(),
            country.name(),
            allowed
        );
    }
}
