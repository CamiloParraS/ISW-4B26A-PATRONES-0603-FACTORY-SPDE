package factory;

import model.CountryCode;
import model.DocumentType;
import model.FileFormat;
import processor.*;

import java.util.List;

public class ArgentinaFactory extends DocumentProcessorFactory {

    @Override
    public CountryCode getCountry() {
        return CountryCode.AR;
    }
    @Override
    protected DocumentProcessor createProcessor(DocumentType documentType, FileFormat fileFormat) {
        List<FileFormat> allowed = getAllowedFormats(documentType);
        return switch (documentType) {
            case ELECTRONIC_INVOICE -> new InvoiceProcessor(getCountry(), allowed);
            case LEGAL_CONTRACT -> new ContractProcessor(getCountry(), allowed);
            case FINANCIAL_REPORT -> new FinancialReportProcessor(getCountry(), allowed);
            case DIGITAL_CERTIFICATE -> new DigitalCertificateProcessor(getCountry(), allowed);
            case TAX_DECLARATION -> new TaxDeclarationProcessor(getCountry(), allowed);
        };
    }

    @Override
    public List<DocumentType> getAllowedDocumentTypes() {
        return List.of(DocumentType.values()); 
    }

    @Override
    public List<FileFormat> getAllowedFormats(DocumentType documentType) {
        return List.of(FileFormat.values()); 
    }
}
