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

public class JapanFactory extends DocumentProcessorFactory {

    private static final List<FileFormat> PDF_ONLY = List.of(FileFormat.PDF);

    private static final Map<DocumentType, List<FileFormat>> FORMAT_RULES =
            Map.of(DocumentType.ELECTRONIC_INVOICE, PDF_ONLY, DocumentType.LEGAL_CONTRACT, PDF_ONLY,
                    DocumentType.FINANCIAL_REPORT, PDF_ONLY, DocumentType.DIGITAL_CERTIFICATE,
                    PDF_ONLY, DocumentType.TAX_DECLARATION, PDF_ONLY);

    @Override
    public CountryCode getCountry() {
        return CountryCode.JP;
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
        List<FileFormat> formats = FORMAT_RULES.get(documentType);
        if (formats == null) {
            throw new IllegalArgumentException("No format rules defined for: " + documentType);
        }
        return formats;
    }
}
