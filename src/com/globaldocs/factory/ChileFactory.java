package com.globaldocs.factory;

import com.globaldocs.model.CountryCode;
import com.globaldocs.model.DocumentType;
import com.globaldocs.model.FileFormat;
import com.globaldocs.processor.*;
import java.util.List;
import java.util.Map;

/**
 * ============================================================
 *  FACTORY METHOD PATTERN — CONCRETE CREATOR (Chile)
 * ============================================================
 * ChileFactory implements country-specific document processing
 * rules aligned with Chilean regulatory requirements (SII).
 *
 * Key rules:
 *  - Electronic Invoices: PDF, DOCX, or XLSX (SII DTE format support)
 *  - Legal Contracts: PDF, DOC, or DOCX
 *  - Financial Reports: PDF, XLSX, or DOCX — CSV is NOT allowed (SII strict)
 *  - Digital Certificates: PDF ONLY (highest restriction — SII security mandate)
 *  - Tax Declarations: PDF, DOCX, or XLSX
 *  - TXT and MD are NOT permitted for any document type in Chile
 * ============================================================
 */
public class ChileFactory extends DocumentProcessorFactory {

    /**
     * Defines allowed file formats per document type for Chile.
     * Strictest country on FINANCIAL_REPORT (no CSV) and DIGITAL_CERTIFICATE (PDF only).
     */
    private static final Map<DocumentType, List<FileFormat>> FORMAT_RULES =
        Map.of(
            DocumentType.ELECTRONIC_INVOICE,
            List.of(FileFormat.PDF, FileFormat.DOCX, FileFormat.XLSX),
            DocumentType.LEGAL_CONTRACT,
            List.of(FileFormat.PDF, FileFormat.DOC, FileFormat.DOCX),
            DocumentType.FINANCIAL_REPORT,
            List.of(FileFormat.PDF, FileFormat.XLSX, FileFormat.DOCX),
            DocumentType.DIGITAL_CERTIFICATE,
            List.of(FileFormat.PDF),
            DocumentType.TAX_DECLARATION,
            List.of(FileFormat.PDF, FileFormat.DOCX, FileFormat.XLSX)
        );

    @Override
    public CountryCode getCountry() {
        return CountryCode.CL;
    }

    /**
     * ── FACTORY METHOD IMPLEMENTATION ───────────────────────
     * Returns the correct DocumentProcessor for Chile.
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
     * All five document types are permitted in Chile.
     */
    @Override
    public List<DocumentType> getAllowedDocumentTypes() {
        return List.of(DocumentType.values());
    }

    /**
     * Returns the allowed formats for the given document type under Chilean regulations.
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
