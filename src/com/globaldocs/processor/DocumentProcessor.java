package com.globaldocs.processor;

import com.globaldocs.model.CountryCode;
import com.globaldocs.model.DocumentType;
import com.globaldocs.model.FileFormat;
import com.globaldocs.model.ProcessingResult;

import java.io.File;
import java.util.List;

/**
 * ============================================================
 *  FACTORY METHOD PATTERN — PRODUCT INTERFACE
 * ============================================================
 * DocumentProcessor is the Product interface in the Factory Method pattern.
 *
 * Each concrete processor (InvoiceProcessor, ContractProcessor, etc.)
 * implements this interface. The factory method in DocumentProcessorFactory
 * returns an instance of this type — the client never depends on
 * a specific implementation, only on this contract.
 *
 * Flow:
 *   DocumentProcessorFactory.createProcessor(type, format)
 *       └── returns DocumentProcessor  ← this interface
 *               └── client calls .process(file)
 * ============================================================
 */
public interface DocumentProcessor {

    /**
     * Processes the given file according to the document type and country rules.
     *
     * @param file the document file to process
     * @return a ProcessingResult containing status, messages, and metadata
     */
    ProcessingResult process(File file);

    /**
     * Returns the document type this processor handles.
     */
    DocumentType getDocumentType();

    /**
     * Returns the country this processor is configured for.
     */
    CountryCode getCountry();

    /**
     * Returns the list of file formats this processor accepts.
     */
    List<FileFormat> getSupportedFormats();

    /**
     * Returns a human-readable description of this processor's purpose.
     */
    String getDescription();
}
