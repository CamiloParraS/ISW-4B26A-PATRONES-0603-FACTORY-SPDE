package com.globaldocs.factory;

import com.globaldocs.model.CountryCode;
import com.globaldocs.model.DocumentType;
import com.globaldocs.model.FileFormat;
import com.globaldocs.processor.*;
import java.util.List;
import java.util.Map;

/**
 * ============================================================
 *  FACTORY METHOD PATTERN — CONCRETE CREATOR (Mexico)
 * ============================================================
 * MexicoFactory implements country-specific document processing
 * rules aligned with Mexican regulatory requirements (SAT/CFDI).
 *
 * Key rules:
 *  - Electronic Invoices must be PDF or XLSX (SAT CFDI structured format)
 *  - Legal Contracts require PDF or DOCX only — no plain text allowed
 *  - Financial Reports allow PDF, XLSX, or CSV (SAT reporting standards)
 *  - Digital Certificates strictly require PDF only
 *  - Tax Declarations require PDF or XLSX (no loose text formats)
 *  - MD and TXT are NOT permitted for any document type in Mexico
 * ============================================================
 */
public class MexicoFactory extends DocumentProcessorFactory {

    /**
     * Defines allowed file formats per document type for Mexico.
     * Notably stricter than Colombia — no .txt or .md anywhere.
     */
    private static final Map<DocumentType, List<FileFormat>> FORMAT_RULES =
        Map.of(
            DocumentType.ELECTRONIC_INVOICE,
            List.of(FileFormat.PDF, FileFormat.XLSX),
            DocumentType.LEGAL_CONTRACT,
            List.of(FileFormat.PDF, FileFormat.DOCX),
            DocumentType.FINANCIAL_REPORT,
            List.of(FileFormat.PDF, FileFormat.XLSX, FileFormat.CSV),
            DocumentType.DIGITAL_CERTIFICATE,
            List.of(FileFormat.PDF),
            DocumentType.TAX_DECLARATION,
            List.of(FileFormat.PDF, FileFormat.XLSX)
        );

    @Override
    public CountryCode getCountry() {
        return CountryCode.MX;
    }

    /**
     * ── FACTORY METHOD IMPLEMENTATION ───────────────────────
     * Returns the correct DocumentProcessor for Mexico.
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
            throw new IllegalArgumentException(
                "No format rules defined for: " + documentType
            );
        }
        return formats;
    }
}
