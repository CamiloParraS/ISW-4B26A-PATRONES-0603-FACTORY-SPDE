package factory;

import java.util.List;
import java.util.Map;
import model.CountryCode;
import model.DocumentType;
import model.FileFormat;
import processor.ContractProcessor;
import processor.DigitalCertificateProcessor;
import processor.DocumentProcessor;
import processor.FinancialReportProcessor;
import processor.InvoiceProcessor;
import processor.TaxDeclarationProcessor;

public class MexicoFactory extends DocumentProcessorFactory {

    private static final Map<DocumentType, List<FileFormat>> FORMAT_RULES = Map.of(
            DocumentType.ELECTRONIC_INVOICE, List.of(FileFormat.PDF, FileFormat.XLSX),
            DocumentType.LEGAL_CONTRACT, List.of(FileFormat.PDF, FileFormat.DOCX),
            DocumentType.FINANCIAL_REPORT, List.of(FileFormat.PDF, FileFormat.XLSX, FileFormat.CSV),
            DocumentType.DIGITAL_CERTIFICATE, List.of(FileFormat.PDF), DocumentType.TAX_DECLARATION,
            List.of(FileFormat.PDF, FileFormat.XLSX));

    @Override
    public CountryCode getCountry() {
        return CountryCode.MX;
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

    /**
     * All five document types are permitted in Mexico.
     */
    @Override
    public List<DocumentType> getAllowedDocumentTypes() {
        return List.of(DocumentType.values());
    }

    /**
     * Returns the allowed formats for the given document type under Mexican regulations.
     */
    @Override
    public List<FileFormat> getAllowedFormats(DocumentType documentType) {
        List<FileFormat> formats = FORMAT_RULES.get(documentType);
        if (formats == null) {
            throw new IllegalArgumentException("No format rules defined for: " + documentType);
        }
        return formats;
    }
}
