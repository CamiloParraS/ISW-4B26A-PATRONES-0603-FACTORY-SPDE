package com.globaldocs.factory;

import com.globaldocs.model.CountryCode;
import com.globaldocs.model.DocumentType;
import com.globaldocs.model.FileFormat;
import java.util.List;
import java.util.Map;

/**
 * ============================================================
 *  VALIDATION RULES MATRIX — Reference & Audit Utility
 * ============================================================
 * Provides a consolidated, read-only view of every country's
 * allowed document type + format combinations.
 *
 * Used for:
 *  - Audit logging
 *  - UI dropdowns (filtering formats by selected country)
 *  - Documentation and compliance checks
 *
 * This class does NOT perform validation itself — each concrete
 * factory owns its rules. This class reads from the registry.
 * ============================================================
 */
public class ValidationRulesMatrix {

    private ValidationRulesMatrix() {
        /* utility class */
    }

    /**
     * Prints a full country-by-country rules matrix to stdout.
     * Useful for documentation and debugging.
     */
    public static void printMatrix() {
        System.out.println(
            "╔══════════════════════════════════════════════════════════════════╗"
        );
        System.out.println(
            "║         GlobalDocs — Country Validation Rules Matrix             ║"
        );
        System.out.println(
            "╚══════════════════════════════════════════════════════════════════╝"
        );

        for (Map.Entry<
            CountryCode,
            DocumentProcessorFactory
        > entry : FactoryRegistry.getAllFactories().entrySet()) {
            CountryCode country = entry.getKey();
            DocumentProcessorFactory factory = entry.getValue();

            System.out.printf(
                "%n▶ %s — %s%n",
                country.name(),
                country.getFullName()
            );
            System.out.println("  " + "─".repeat(60));

            for (DocumentType docType : factory.getAllowedDocumentTypes()) {
                List<FileFormat> formats = factory.getAllowedFormats(docType);
                String formatList = formats
                    .stream()
                    .map(FileFormat::getExtension)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("none");
                System.out.printf(
                    "  %-26s → [%s]%n",
                    docType.getDisplayName(),
                    formatList
                );
            }
        }

        System.out.println();
    }

    /**
     * Returns true if the given combination is valid for the specified country.
     * Convenience method for UI components and tests.
     */
    public static boolean isValid(
        CountryCode country,
        DocumentType docType,
        FileFormat format
    ) {
        try {
            DocumentProcessorFactory factory = FactoryRegistry.getFactory(
                country
            );
            return (
                factory.getAllowedDocumentTypes().contains(docType) &&
                factory.getAllowedFormats(docType).contains(format)
            );
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns the allowed formats for a given country + document type.
     * Safe to call from UI components without triggering exceptions.
     */
    public static List<FileFormat> getAllowedFormats(
        CountryCode country,
        DocumentType docType
    ) {
        return FactoryRegistry.getFactory(country).getAllowedFormats(docType);
    }
}
