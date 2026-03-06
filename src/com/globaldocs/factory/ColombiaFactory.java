package com.globaldocs.factory;

import com.globaldocs.model.CountryCode;
import com.globaldocs.model.DocumentType;
import com.globaldocs.model.FileFormat;
import com.globaldocs.processor.*;
import java.util.List;
import java.util.Map;

/**
 * ============================================================
 *  FACTORY METHOD PATTERN — CONCRETE CREATOR (Colombia)
 * ============================================================
 * ColombiaFactory implements country-specific document processing
 * rules aligned with Colombian regulatory requirements (DIAN).
 *
 * Key rules:
 *  - Electronic Invoices must be PDF or DOCX (DIAN e-invoicing mandate)
 *  - Digital Certificates only accept PDF (security compliance)
 *  - Tax Declarations require structured formats: PDF, XLSX, or CSV
 *  - Legal Contracts allow DOC/DOCX/PDF for legacy compatibility
 *  - Financial Reports allow PDF, XLSX, CSV, or TXT
 * ============================================================
 */
public class ColombiaFactory extends DocumentProcessorFactory {

    /**
     * Defines allowed file formats per document type for Colombia.
     * This map is the single source of truth for CO validation rules.
     */
    private static final Map<DocumentType, List<FileFormat>> FORMAT_RULES =
        Map.of(
            DocumentType.ELECTRONIC_INVOICE,
            List.of(FileFormat.PDF, FileFormat.DOCX),
            DocumentType.LEGAL_CONTRACT,
            List.of(FileFormat.PDF, FileFormat.DOC, FileFormat.DOCX),
            DocumentType.FINANCIAL_REPORT,
            List.of(
                FileFormat.PDF,
                FileFormat.XLSX,
                FileFormat.CSV,
                FileFormat.TXT
            ),
            DocumentType.DIGITAL_CERTIFICATE,
            List.of(FileFormat.PDF),
            DocumentType.TAX_DECLARATION,
            List.of(FileFormat.PDF, FileFormat.XLSX, FileFormat.CSV)
        );

    @Override
    public CountryCode getCountry() {
        return CountryCode.CO;
    }

    /**
     * ── FACTORY METHOD IMPLEMENTATION ───────────────────────
     * Returns the correct DocumentProcessor for Colombia.
     * Called internally by getProcessor() only after validation passes.
     * ← This is where the Factory Method pattern creates the Product.
     */
    @Override
    protected DocumentProcessor createProcessor(
        DocumentType documentType,
        FileFormat fileFormat
    ) {
        List<FileFormat> allowed = getAllowedFormats(documentType);
        return switch (documentType) {
            case ELECTRONIC_INVOICE -> new InvoiceProcessor(
                getCountry(),
                allowed
            );
            case LEGAL_CONTRACT -> new ContractProcessor(getCountry(), allowed);
            case FINANCIAL_REPORT -> new FinancialReportProcessor(
                getCountry(),
                allowed
            );
            case DIGITAL_CERTIFICATE -> new DigitalCertificateProcessor(
                getCountry(),
                allowed
            );
            case TAX_DECLARATION -> new TaxDeclarationProcessor(
                getCountry(),
                allowed
            );
        };
    }

    /**
     * All five document types are permitted in Colombia.
     */
    @Override
    public List<DocumentType> getAllowedDocumentTypes() {
        return List.of(DocumentType.values());
    }

    /**
     * Returns the allowed formats for the given document type under Colombian regulations.
     * Throws IllegalArgumentException if the document type is unknown.
     */
    @Override
    public List<FileFormat> getAllowedFormats(DocumentType documentType) {
        List<FileFormat> formats = FORMAT_RULES.get(documentType);
        if (formats == null) {
            throw new IllegalArgumentException(
                "No format rules defined for: " + documentType
            );
        }
        return formats;
    }
}
