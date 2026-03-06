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

public class ChileFactory extends DocumentProcessorFactory {

    private static final Map<DocumentType, List<FileFormat>> FORMAT_RULES = Map.of(
            DocumentType.ELECTRONIC_INVOICE,
            List.of(FileFormat.PDF, FileFormat.DOCX, FileFormat.XLSX), DocumentType.LEGAL_CONTRACT,
            List.of(FileFormat.PDF, FileFormat.DOC, FileFormat.DOCX), DocumentType.FINANCIAL_REPORT,
            List.of(FileFormat.PDF, FileFormat.XLSX, FileFormat.DOCX),
            DocumentType.DIGITAL_CERTIFICATE, List.of(FileFormat.PDF), DocumentType.TAX_DECLARATION,
            List.of(FileFormat.PDF, FileFormat.DOCX, FileFormat.XLSX));

    @Override
    public CountryCode getCountry() {
        return CountryCode.CL;
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
