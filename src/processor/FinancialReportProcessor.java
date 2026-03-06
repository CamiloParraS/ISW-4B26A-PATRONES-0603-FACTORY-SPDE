package processor;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import model.CountryCode;
import model.DocumentType;
import model.FileFormat;
import model.ProcessStatus;
import model.ProcessingResult;

/**
 * ============================================================ CONCRETE PRODUCT — Financial Report
 * Processor ============================================================ Handles processing of
 * Financial Report documents. Instantiated by each country factory via the Factory Method.
 *
 * Processing logic: - Validates file is not empty - Detects report period from filename (Q1–Q4, or
 * annual) - Estimates row/column count for structured formats (XLSX, CSV) - Applies
 * country-specific accounting standard label - Simulates balance sheet integrity check - Returns
 * detailed result with financial metadata
 * ============================================================
 */
public class FinancialReportProcessor extends BaseDocumentProcessor {

    private static final long MIN_SIZE_BYTES = 256L;

    // Accounting standards per country
    private static final java.util.Map<CountryCode, String> ACCOUNTING_STANDARD =
            java.util.Map.of(CountryCode.CO, "NIIF (IFRS Colombia)", CountryCode.MX,
                    "NIF (Normas de Información Financiera)", CountryCode.AR,
                    "RT FACPCE (Resoluciones Técnicas)", CountryCode.CL, "IFRS Chile (SVS)");

    public FinancialReportProcessor(CountryCode country, List<FileFormat> supportedFormats) {
        super(country, supportedFormats);
    }

    /**
     * Core financial report processing logic.
     */
    @Override
    protected ProcessingResult doProcess(File file) {
        long fileSize = resolveFileSize(file);

        if (fileSize < MIN_SIZE_BYTES) {
            return failedResult(file,
                    String.format("Financial report '%s' appears empty (%d bytes).", file.getName(),
                            fileSize),
                    ProcessStatus.FAILED);
        }

        FileFormat format = resolveFormat(file);
        String standard = ACCOUNTING_STANDARD.getOrDefault(country, "Local GAAP");
        String reportPeriod = detectReportPeriod(file);
        String dataShape = describeDataShape(fileSize, format);
        String reportRef = generateReportRef(file);

        String message = String.format(
                "Financial Report processed successfully. "
                        + "Ref: %s | Standard: %s | Period: %s | %s | "
                        + "Currency validation: PASSED | Audit trail: GENERATED | Filed: %s",
                reportRef, standard, reportPeriod, dataShape, LocalDate.now());

        return successResult(file, message);
    }

    @Override
    public DocumentType getDocumentType() {
        return DocumentType.FINANCIAL_REPORT;
    }

    @Override
    public String getDescription() {
        return String.format("Financial Report Processor for %s — standard: %s",
                country.getFullName(), ACCOUNTING_STANDARD.getOrDefault(country, "Local GAAP"));
    }

    // ── Simulation Helpers ───────────────────────────────────

    private String generateReportRef(File file) {
        int hash = Math.abs(file.getName().hashCode()) % 90000 + 10000;
        return "FIN-" + country.name() + "-" + hash + "-" + LocalDate.now().getYear();
    }

    /**
     * Infers the reporting period from the filename. Recognizes: Q1–Q4, annual, semestral patterns.
     */
    private String detectReportPeriod(File file) {
        String name = file.getName().toLowerCase();
        if (name.contains("q1") || name.contains("primer"))
            return "Q1 " + LocalDate.now().getYear();
        if (name.contains("q2") || name.contains("segundo"))
            return "Q2 " + LocalDate.now().getYear();
        if (name.contains("q3") || name.contains("tercer"))
            return "Q3 " + LocalDate.now().getYear();
        if (name.contains("q4") || name.contains("cuarto"))
            return "Q4 " + LocalDate.now().getYear();
        if (name.contains("annual") || name.contains("anual"))
            return "Annual " + LocalDate.now().getYear();
        if (name.contains("semi") || name.contains("h1") || name.contains("h2"))
            return "Semi-annual " + LocalDate.now().getYear();
        return "Period: " + LocalDate.now().getYear() + " (unspecified quarter)";
    }

    /**
     * Describes estimated data dimensions for structured formats.
     */
    private String describeDataShape(long sizeBytes, FileFormat format) {
        return switch (format) {
            case XLSX -> {
                int rows = (int) Math.max(10, sizeBytes / 200);
                int cols = 12 + (int) (sizeBytes % 7);
                yield String.format("Spreadsheet: ~%d rows × %d columns", rows, cols);
            }
            case CSV -> {
                int rows = (int) Math.max(5, sizeBytes / 80);
                yield String.format("CSV: ~%d data rows", rows);
            }
            case PDF -> {
                int pages = (int) Math.max(1, sizeBytes / 60_000);
                yield String.format("PDF: ~%d pages", pages);
            }
            default -> "Text document";
        };
    }
}

