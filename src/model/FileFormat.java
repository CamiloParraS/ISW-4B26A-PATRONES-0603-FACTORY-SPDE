package model;

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
