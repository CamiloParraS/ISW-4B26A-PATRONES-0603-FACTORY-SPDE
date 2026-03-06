package factory;

import java.util.List;
import exception.InvalidDocumentForCountryException;
import exception.UnsupportedFileFormatException;
import model.CountryCode;
import model.DocumentType;
import model.FileFormat;
import processor.DocumentProcessor;

public abstract class DocumentProcessorFactory {

    protected abstract DocumentProcessor createProcessor(DocumentType documentType,
            FileFormat fileFormat);

    public DocumentProcessor getProcessor(DocumentType documentType, FileFormat fileFormat,
            String documentName) {
        validateDocumentType(documentType, documentName);
        validateFileFormat(documentType, fileFormat, documentName);
        return createProcessor(documentType, fileFormat);
    }

    public abstract CountryCode getCountry();

    public abstract List<DocumentType> getAllowedDocumentTypes();

    public abstract List<FileFormat> getAllowedFormats(DocumentType documentType);

    protected void validateDocumentType(DocumentType documentType, String documentName) {
        if (!getAllowedDocumentTypes().contains(documentType)) {
            throw new InvalidDocumentForCountryException(getCountry(), documentType, documentName);
        }
    }

    protected void validateFileFormat(DocumentType documentType, FileFormat fileFormat,
            String documentName) {
        List<FileFormat> allowed = getAllowedFormats(documentType);
        if (!allowed.contains(fileFormat)) {
            throw new UnsupportedFileFormatException(getCountry(), documentType, fileFormat,
                    allowed, documentName);
        }
    }

    @Override
    public String toString() {
        return String.format("DocumentProcessorFactory[%s — %s]", getCountry().name(),
                getCountry().getFullName());
    }
}
