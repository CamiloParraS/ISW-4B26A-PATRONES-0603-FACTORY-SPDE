package processor;

import model.*;

import java.io.File;
import java.util.List;

public class ContractProcessor implements DocumentProcessor {

    private final CountryCode      country;
    private final List<FileFormat> supportedFormats;

    public ContractProcessor(CountryCode country, List<FileFormat> supportedFormats) {
        this.country          = country;
        this.supportedFormats = supportedFormats;
    }

    @Override
    public ProcessingResult process(File file) {
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
