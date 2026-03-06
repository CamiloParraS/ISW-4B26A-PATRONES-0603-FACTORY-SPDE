package com.globaldocs.processor;

import com.globaldocs.model.*;

import java.io.File;
import java.util.List;

/**
 * Concrete Product — processes Legal Contract documents.
 * Business logic will be implemented in Phase 3.
 *
 * Instantiated by each country factory via the Factory Method.
 */
public class ContractProcessor implements DocumentProcessor {

    private final CountryCode      country;
    private final List<FileFormat> supportedFormats;

    public ContractProcessor(CountryCode country, List<FileFormat> supportedFormats) {
        this.country          = country;
        this.supportedFormats = supportedFormats;
    }

    @Override
    public ProcessingResult process(File file) {
        // TODO (Phase 3): Implement contract processing logic
        throw new UnsupportedOperationException("ContractProcessor.process() not yet implemented — Phase 3");
    }

    @Override
    public DocumentType getDocumentType() {
        return DocumentType.LEGAL_CONTRACT;
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
        return "Processes Legal Contract documents for " + country.getFullName();
    }
}
