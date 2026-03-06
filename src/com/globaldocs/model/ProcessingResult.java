package com.globaldocs.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Immutable result object returned by every DocumentProcessor.process() call.
 * Carries all metadata about the processing outcome for display and reporting.
 */
public class ProcessingResult {

    private final String        documentId;
    private final String        documentName;
    private final CountryCode   country;
    private final DocumentType  documentType;
    private final FileFormat    fileFormat;
    private final ProcessStatus status;
    private final String        message;
    private final LocalDateTime processedAt;
    private final long          fileSizeBytes;

    // Private constructor — use the Builder
    private ProcessingResult(Builder builder) {
        this.documentId    = builder.documentId;
        this.documentName  = builder.documentName;
        this.country       = builder.country;
        this.documentType  = builder.documentType;
        this.fileFormat    = builder.fileFormat;
        this.status        = builder.status;
        this.message       = builder.message;
        this.processedAt   = builder.processedAt;
        this.fileSizeBytes = builder.fileSizeBytes;
    }

    // ── Getters ──────────────────────────────────────────────

    public String        getDocumentId()    { return documentId; }
    public String        getDocumentName()  { return documentName; }
    public CountryCode   getCountry()       { return country; }
    public DocumentType  getDocumentType()  { return documentType; }
    public FileFormat    getFileFormat()    { return fileFormat; }
    public ProcessStatus getStatus()        { return status; }
    public String        getMessage()       { return message; }
    public LocalDateTime getProcessedAt()   { return processedAt; }
    public long          getFileSizeBytes() { return fileSizeBytes; }

    public boolean isSuccess() {
        return status == ProcessStatus.SUCCESS;
    }

    /**
     * Returns a formatted summary of this result for UI display.
     */
    public String toDisplayString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format(
            "┌─ Processing Result ────────────────────────────┐%n" +
            "  ID       : %s%n" +
            "  Document : %s%n" +
            "  Country  : %s%n" +
            "  Type     : %s%n" +
            "  Format   : %s%n" +
            "  Status   : %s%n" +
            "  Message  : %s%n" +
            "  Size     : %d bytes%n" +
            "  Time     : %s%n" +
            "└────────────────────────────────────────────────┘",
            documentId,
            documentName,
            country.name(),
            documentType.getDisplayName(),
            fileFormat.getExtension(),
            status.name(),
            message,
            fileSizeBytes,
            processedAt.format(fmt)
        );
    }

    // ── Builder ──────────────────────────────────────────────

    public static class Builder {

        private String        documentId    = UUID.randomUUID().toString();
        private String        documentName;
        private CountryCode   country;
        private DocumentType  documentType;
        private FileFormat    fileFormat;
        private ProcessStatus status;
        private String        message       = "";
        private LocalDateTime processedAt   = LocalDateTime.now();
        private long          fileSizeBytes = 0L;

        public Builder documentName(String documentName)   { this.documentName  = documentName;  return this; }
        public Builder country(CountryCode country)        { this.country        = country;       return this; }
        public Builder documentType(DocumentType type)     { this.documentType  = type;           return this; }
        public Builder fileFormat(FileFormat format)       { this.fileFormat    = format;         return this; }
        public Builder status(ProcessStatus status)        { this.status        = status;         return this; }
        public Builder message(String message)             { this.message       = message;        return this; }
        public Builder processedAt(LocalDateTime time)     { this.processedAt   = time;           return this; }
        public Builder fileSizeBytes(long size)            { this.fileSizeBytes = size;           return this; }

        public ProcessingResult build() {
            if (documentName == null) throw new IllegalStateException("documentName is required");
            if (country       == null) throw new IllegalStateException("country is required");
            if (documentType  == null) throw new IllegalStateException("documentType is required");
            if (fileFormat    == null) throw new IllegalStateException("fileFormat is required");
            if (status        == null) throw new IllegalStateException("status is required");
            return new ProcessingResult(this);
        }
    }
}
