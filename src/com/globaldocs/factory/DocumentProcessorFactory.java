package com.globaldocs.factory;

import com.globaldocs.exception.InvalidDocumentForCountryException;
import com.globaldocs.exception.UnsupportedFileFormatException;
import com.globaldocs.model.CountryCode;
import com.globaldocs.model.DocumentType;
import com.globaldocs.model.FileFormat;
import com.globaldocs.processor.DocumentProcessor;
import java.util.List;

/**
 * ============================================================
 *  FACTORY METHOD PATTERN — ABSTRACT CREATOR
 * ============================================================
 * DocumentProcessorFactory is the Creator in the Factory Method pattern.
 *
 * It declares the factory method: createProcessor(DocumentType, FileFormat)
 * Each concrete subclass (ColombiaFactory, MexicoFactory, etc.) overrides
 * this method to return the appropriate DocumentProcessor for that country.
 *
 * The factory also provides:
 *  - Country-level validation hooks (validateDocumentType, validateFileFormat)
 *  - A template method (getProcessor) that validates before creating
 *
 * Subclass responsibilities:
 *  - Override createProcessor() — the core Factory Method
 *  - Override getAllowedDocumentTypes() — country-specific allowed types
 *  - Override getAllowedFormats(DocumentType) — per-type format rules
 * ============================================================
 */
public abstract class DocumentProcessorFactory {

    /**
     * ── FACTORY METHOD ──────────────────────────────────────
     * The core factory method. Each country subclass overrides this
     * to return the concrete DocumentProcessor for that country.
     *
     * This method should NOT be called directly by clients.
     * Use getProcessor() instead, which wraps validation + creation.
     *
     * @param documentType the type of document to process
     * @param fileFormat   the file format of the document
     * @return a DocumentProcessor configured for this country
     */
    protected abstract DocumentProcessor createProcessor(
        DocumentType documentType,
        FileFormat fileFormat
    );

    /**
     * ── TEMPLATE METHOD ─────────────────────────────────────
     * Public entry point for clients. Validates country rules first,
     * then delegates to the factory method (createProcessor).
     *
     * This ensures no client can bypass validation.
     *
     * @param documentType the type of document to process
     * @param fileFormat   the file format of the document
     * @param documentName the original filename (for error messages)
     * @return a validated, country-specific DocumentProcessor
     * @throws InvalidDocumentForCountryException if the document type is not allowed
     * @throws UnsupportedFileFormatException     if the format is not allowed for the type
     */
    public DocumentProcessor getProcessor(
        DocumentType documentType,
        FileFormat fileFormat,
        String documentName
    ) {
        validateDocumentType(documentType, documentName);
        validateFileFormat(documentType, fileFormat, documentName);
        // ← Factory Method called here after validation passes
        return createProcessor(documentType, fileFormat);
    }

    /**
     * Returns the country this factory is responsible for.
     */
    public abstract CountryCode getCountry();

    /**
     * Returns all document types permitted for this country.
     * Subclasses define country-specific rules here.
     */
    public abstract List<DocumentType> getAllowedDocumentTypes();

    /**
     * Returns all file formats allowed for a given document type in this country.
     * Subclasses define per-type format restrictions here.
     *
     * @param documentType the document type to query
     */
    public abstract List<FileFormat> getAllowedFormats(
        DocumentType documentType
    );

    // ── Validation Helpers ───────────────────────────────────

    /**
     * Validates that the given document type is allowed in this country.
     * Throws InvalidDocumentForCountryException if not.
     */
    protected void validateDocumentType(
        DocumentType documentType,
        String documentName
    ) {
        if (!getAllowedDocumentTypes().contains(documentType)) {
            throw new InvalidDocumentForCountryException(
                getCountry(),
                documentType,
                documentName
            );
        }
    }

    /**
     * Validates that the file format is allowed for the given document type in this country.
     * Throws UnsupportedFileFormatException if not.
     */
    protected void validateFileFormat(
        DocumentType documentType,
        FileFormat fileFormat,
        String documentName
    ) {
        List<FileFormat> allowed = getAllowedFormats(documentType);
        if (!allowed.contains(fileFormat)) {
            throw new UnsupportedFileFormatException(
                getCountry(),
                documentType,
                fileFormat,
                allowed,
                documentName
            );
        }
    }

    @Override
    public String toString() {
        return String.format(
            "DocumentProcessorFactory[%s — %s]",
            getCountry().name(),
            getCountry().getFullName()
        );
    }
}
