package model;

import java.io.File;
import java.util.Objects;

public class BatchItem {

    private final String itemId; // unique ID within the batch
    private final CountryCode country;
    private final DocumentType documentType;
    private final FileFormat fileFormat;
    private final File file;

    public BatchItem(String itemId, CountryCode country, DocumentType documentType,
            FileFormat fileFormat, File file) {
        if (itemId == null || itemId.isBlank())
            throw new IllegalArgumentException("itemId is required");
        if (country == null)
            throw new IllegalArgumentException("country is required");
        if (documentType == null)
            throw new IllegalArgumentException("documentType is required");
        if (fileFormat == null)
            throw new IllegalArgumentException("fileFormat is required");
        if (file == null)
            throw new IllegalArgumentException("file is required");

        this.itemId = itemId;
        this.country = country;
        this.documentType = documentType;
        this.fileFormat = fileFormat;
        this.file = file;
    }

    public BatchItem(int index, CountryCode country, DocumentType documentType,
            FileFormat fileFormat, File file) {
        this("ITEM-" + String.format("%03d", index), country, documentType, fileFormat, file);
    }

    public String getItemId() {
        return itemId;
    }

    public CountryCode getCountry() {
        return country;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public FileFormat getFileFormat() {
        return fileFormat;
    }

    public File getFile() {
        return file;
    }

    public String getLabel() {
        return String.format("[%s] %s | %s | %s", itemId, country.name(),
                documentType.getDisplayName(), file.getName());
    }

    @Override
    public String toString() {
        return getLabel();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof BatchItem))
            return false;
        return Objects.equals(itemId, ((BatchItem) o).itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId);
    }
}
