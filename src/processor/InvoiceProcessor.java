package processor;

import java.io.File;
import java.util.List;
import model.CountryCode;
import model.DocumentType;
import model.FileFormat;
import model.ProcessStatus;
import model.ProcessingResult;

public class InvoiceProcessor extends BaseDocumentProcessor {

    private static final long MIN_SIZE_BYTES = 512L;

    private static final java.util.Map<CountryCode, String> AUTHORITY =
            java.util.Map.of(CountryCode.CO, "DIAN", // Colombia — Dirección de Impuestos y Aduanas
                                                     // // Nacionales ---- I hate you autoformatter
                    CountryCode.MX, "SAT", // Mexico — Servicio de Administración Tributaria
                    CountryCode.AR, "AFIP", // Argentina — Administración Federal de Ingresos
                                            // Públicos
                    CountryCode.CL, "SII" // Chile — Servicio de Impuestos Internos
            );

    public InvoiceProcessor(CountryCode country, List<FileFormat> supportedFormats) {
        super(country, supportedFormats);
    }

    @Override
    protected ProcessingResult doProcess(File file) {
        long fileSize = resolveFileSize(file);

        // Guard: file must not be suspiciously small
        if (fileSize < MIN_SIZE_BYTES) {
            return failedResult(file, String.format(
                    "File '%s' appears to be empty or corrupt (%d bytes). Minimum required: %d bytes.",
                    file.getName(), fileSize, MIN_SIZE_BYTES), ProcessStatus.FAILED);
        }

        String invoiceNumber = generateInvoiceNumber(file);
        String authority = AUTHORITY.getOrDefault(country, "REGULATORY_BODY");
        FileFormat format = resolveFormat(file);

        String message = String.format(
                "Electronic Invoice processed successfully. "
                        + "Authority: %s | Invoice #: %s | Format: %s | "
                        + "Pages: %d | Fiscal year: %d | Tax validation: PASSED",
                authority, invoiceNumber, format.getExtension(),
                simulatePageCount(fileSize, format), java.time.LocalDate.now().getYear());

        return successResult(file, message);
    }

    @Override
    public DocumentType getDocumentType() {
        return DocumentType.ELECTRONIC_INVOICE;
    }

    @Override
    public String getDescription() {
        return String.format("Electronic Invoice Processor for %s (%s)", country.getFullName(),
                AUTHORITY.getOrDefault(country, "N/A"));
    }

    private String generateInvoiceNumber(File file) {
        String prefix = switch (country) {
            case CO -> "FE"; // Factura Electrónica
            case MX -> "CFDI"; // Comprobante Fiscal Digital por Internet
            case AR -> "FC"; // Factura Clase C
            case CL -> "DTE"; // Documento Tributario Electrónico
        };
        int hash = Math.abs(file.getName().hashCode()) % 900000 + 100000;
        return prefix + "-" + country.name() + "-" + hash;
    }

    private int simulatePageCount(long sizeBytes, FileFormat format) {
        long bytesPerPage = switch (format) {
            case PDF -> 60_000L;
            case DOCX -> 25_000L;
            case XLSX -> 15_000L;
            default -> 10_000L;
        };
        return (int) Math.max(1, sizeBytes / bytesPerPage);
    }
}

