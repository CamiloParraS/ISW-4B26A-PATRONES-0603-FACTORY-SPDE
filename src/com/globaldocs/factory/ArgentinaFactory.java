package com.globaldocs.factory;

import com.globaldocs.model.CountryCode;
import com.globaldocs.model.DocumentType;
import com.globaldocs.model.FileFormat;
import com.globaldocs.processor.*;
import java.util.List;
import java.util.Map;

/**
 * ============================================================
 *  FACTORY METHOD PATTERN — CONCRETE CREATOR (Argentina)
 * ============================================================
 * ArgentinaFactory implements country-specific document processing
 * rules aligned with Argentinian regulatory requirements (AFIP).
 *
 * Key rules:
 *  - Electronic Invoices: PDF, DOCX, or CSV (AFIP e-billing flexibility)
 *  - Legal Contracts: PDF, DOC, or DOCX (legacy .doc still accepted)
 *  - Financial Reports: PDF, XLSX, CSV, or TXT (broad AFIP acceptance)
 *  - Digital Certificates: PDF or DOCX only
 *  - Tax Declarations: PDF or DOCX only — .doc, .csv NOT allowed (AFIP strict mode)
 *  - XLSX is NOT permitted for Tax Declarations in Argentina
 * ============================================================
 */
public class ArgentinaFactory extends DocumentProcessorFactory {

    /**
     * Defines allowed file formats per document type for Argentina.
     * Note: TAX_DECLARATION is more restrictive than other countries.
     */
    private static final Map<DocumentType, List<FileFormat>> FORMAT_RULES =
        Map.of(
            DocumentType.ELECTRONIC_INVOICE,
            List.of(FileFormat.PDF, FileFormat.DOCX, FileFormat.CSV),
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
            List.of(FileFormat.PDF, FileFormat.DOCX),
            DocumentType.TAX_DECLARATION,
            List.of(FileFormat.PDF, FileFormat.DOCX)
        );

    @Override
    public CountryCode getCountry() {
        return CountryCode.AR;
    }

    /**
     * ── FACTORY METHOD IMPLEMENTATION ───────────────────────
     * Returns the correct DocumentProcessor for Argentina.
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
     * All five document types are permitted in Argentina.
     */
    @Override
    public List<DocumentType> getAllowedDocumentTypes() {
        return List.of(DocumentType.values());
    }

    /**
     * Returns the allowed formats for the given document type under Argentinian regulations.
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
