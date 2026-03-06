package com.globaldocs.model;

/**
 * Enum representing all supported file formats for document uploads.
 * Factories use this to validate format compatibility per country and document type.
 */
public enum FileFormat {

    PDF(".pdf"),
    DOC(".doc"),
    DOCX(".docx"),
    MD(".md"),
    CSV(".csv"),
    TXT(".txt"),
    XLSX(".xlsx");

    private final String extension;

    FileFormat(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    /**
     * Resolves a FileFormat from a filename or extension string.
     * Example: "invoice.pdf" or ".pdf" → FileFormat.PDF
     *
     * @param filename the filename or extension to resolve
     * @return the matching FileFormat
     * @throws IllegalArgumentException if no match is found
     */
    public static FileFormat fromFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Filename cannot be null or blank.");
        }
        String lower = filename.toLowerCase().trim();
        for (FileFormat format : values()) {
            if (lower.endsWith(format.extension)) {
                return format;
            }
        }
        throw new IllegalArgumentException("Unsupported file format: " + filename);
    }

    @Override
    public String toString() {
        return extension;
    }
}
