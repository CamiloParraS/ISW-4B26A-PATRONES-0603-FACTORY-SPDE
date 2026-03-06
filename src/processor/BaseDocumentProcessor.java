package processor;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import exception.DocumentProcessingException;
import exception.UnsupportedFileFormatException;
import model.CountryCode;
import model.FileFormat;
import model.ProcessStatus;
import model.ProcessingResult;

public abstract class BaseDocumentProcessor implements DocumentProcessor {

    protected final CountryCode country;
    protected final List<FileFormat> supportedFormats;

    protected BaseDocumentProcessor(CountryCode country, List<FileFormat> supportedFormats) {
        this.country = country;
        this.supportedFormats = supportedFormats;
    }

    @Override
    public final ProcessingResult process(File file) {
        try {
            validateFile(file);

            return doProcess(file);
        } catch (UnsupportedFileFormatException e) {
            return failedResult(file, e.getMessage(), ProcessStatus.SKIPPED);
        } catch (DocumentProcessingException e) {
            return failedResult(file, e.getMessage(), ProcessStatus.FAILED);
        } catch (Exception e) {
            return failedResult(file, "Unexpected error during processing: "
                    + e.getClass().getSimpleName() + " — " + e.getMessage(), ProcessStatus.FAILED);
        }
    }

    protected abstract ProcessingResult doProcess(File file);

    protected void validateFile(File file) {
        if (file == null) {
            throw new DocumentProcessingException("File cannot be null.", "null", country.name());
        }

        FileFormat format;
        try {
            format = FileFormat.fromFilename(file.getName());
        } catch (IllegalArgumentException e) {
            throw new DocumentProcessingException(
                    "Cannot determine file format from filename: " + file.getName(), file.getName(),
                    country.name());
        }

        if (!supportedFormats.contains(format)) {
            throw new UnsupportedFileFormatException(country, getDocumentType(), format,
                    supportedFormats, file.getName());
        }
    }


    protected long resolveFileSize(File file) {
        if (file.exists()) {
            return file.length();
        }
        return switch (FileFormat.fromFilename(file.getName())) {
            case PDF -> 245_760L; // ~240 KB
            case DOCX -> 89_088L; // ~87 KB
            case DOC -> 102_400L; // ~100 KB
            case XLSX -> 51_200L; // ~50 KB
            case CSV -> 12_288L; // ~12 KB
            case TXT -> 4_096L; // ~4 KB
            case MD -> 2_048L; // ~2 KB
        };
    }

    protected FileFormat resolveFormat(File file) {
        return FileFormat.fromFilename(file.getName());
    }

    protected ProcessingResult successResult(File file, String message) {
        return new ProcessingResult.Builder().documentName(file.getName()).country(country)
                .documentType(getDocumentType()).fileFormat(resolveFormat(file))
                .status(ProcessStatus.SUCCESS).message(message).fileSizeBytes(resolveFileSize(file))
                .processedAt(LocalDateTime.now()).build();
    }


    protected ProcessingResult failedResult(File file, String errorMessage, ProcessStatus status) {
        String name = (file != null) ? file.getName() : "unknown";
        FileFormat fmt = FileFormat.PDF; // fallback for failed results
        try {
            if (file != null)
                fmt = resolveFormat(file);
        } catch (Exception ignored) {
            /* use fallback */
        }

        return new ProcessingResult.Builder().documentName(name).country(country)
                .documentType(getDocumentType()).fileFormat(fmt).status(status)
                .message(errorMessage).fileSizeBytes(0L).processedAt(LocalDateTime.now()).build();
    }


    @Override
    public CountryCode getCountry() {
        return country;
    }

    @Override
    public List<FileFormat> getSupportedFormats() {
        return supportedFormats;
    }
}
