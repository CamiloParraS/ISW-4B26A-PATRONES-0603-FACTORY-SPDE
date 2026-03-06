package com.globaldocs.processor;

import com.globaldocs.model.*;

import java.io.File;
import java.util.List;

/**
 * Concrete Product — processes Digital Certificate documents.
 * Business logic will be implemented in Phase 3.
 *
 * Instantiated by each country factory via the Factory Method.
 */
public class DigitalCertificateProcessor implements DocumentProcessor {

    private final CountryCode      country;
    private final List<FileFormat> supportedFormats;

    public DigitalCertificateProcessor(CountryCode country, List<FileFormat> supportedFormats) {
        this.country          = country;
        this.supportedFormats = supportedFormats;
    }

    @Override
    public ProcessingResult process(File file) {
        // TODO (Phase 3): Implement digital certificate processing logic
        throw new UnsupportedOperationException("DigitalCertificateProcessor.process() not yet implemented — Phase 3");
    }

    @Override
    public DocumentType getDocumentType() {
        return DocumentType.DIGITAL_CERTIFICATE;
    }

    @Override
    public CountryCode getCountry() {
        return country;
    }

    @Override
    public List<FileFormat> getSupportedFormats() {
        return supportedFormats;
    }

    @Override
    public String getDescription() {
        return "Processes Digital Certificate documents for " + country.getFullName();
    }
}
