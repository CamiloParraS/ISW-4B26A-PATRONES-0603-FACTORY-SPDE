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
 * ============================================================ CONCRETE PRODUCT — Legal Contract
 * Processor ============================================================ Handles processing of
 * Legal Contract documents. Instantiated by each country factory via the Factory Method.
 *
 * Processing logic: - Validates contract is not empty - Simulates clause detection (estimated
 * clause count by file size) - Assigns a contract reference ID with country-specific prefix -
 * Checks for mandatory fields: parties, date, jurisdiction - Applies country-specific legal
 * framework label - Returns detailed result with contract metadata
 * ============================================================
 */
public class ContractProcessor extends BaseDocumentProcessor {

    private static final long MIN_SIZE_BYTES = 1_024L; // Contracts must be at least 1KB

    // Country-specific legal framework references
    private static final java.util.Map<CountryCode, String> LEGAL_FRAMEWORK =
            java.util.Map.of(CountryCode.CO, "Código Civil Colombiano", CountryCode.MX,
                    "Código Civil Federal Mexicano", CountryCode.AR,
                    "Código Civil y Comercial Argentino", CountryCode.CL, "Código Civil Chileno");

    public ContractProcessor(CountryCode country, List<FileFormat> supportedFormats) {
        super(country, supportedFormats);
    }

    /**
     * Core contract processing logic. Validates structure, simulates clause extraction, builds
     * result.
     */
    @Override
    protected ProcessingResult doProcess(File file) {
        long fileSize = resolveFileSize(file);

        // Guard: contracts must have substantial content
        if (fileSize < MIN_SIZE_BYTES) {
            return failedResult(file, String.format(
                    "Contract '%s' is too small (%d bytes). Legal contracts require at least %d bytes.",
                    file.getName(), fileSize, MIN_SIZE_BYTES), ProcessStatus.FAILED);
        }

        FileFormat format = resolveFormat(file);
        String contractRef = generateContractRef(file);
        String framework = LEGAL_FRAMEWORK.getOrDefault(country, "Local Civil Code");
        int estimatedClauses = estimateClauses(fileSize, format);
        int estimatedPages = estimatePages(fileSize, format);

        String message = String.format(
                "Legal Contract processed successfully. "
                        + "Ref: %s | Framework: %s | Est. clauses: %d | "
                        + "Est. pages: %d | Jurisdiction: %s | Signature fields: %s | Date: %s",
                contractRef, framework, estimatedClauses, estimatedPages, country.getFullName(),
                detectSignatureFields(file), LocalDate.now());

        return successResult(file, message);
    }

    @Override
    public DocumentType getDocumentType() {
        return DocumentType.LEGAL_CONTRACT;
    }

    @Override
    public String getDescription() {
        return String.format("Legal Contract Processor for %s — governed by %s",
                country.getFullName(), LEGAL_FRAMEWORK.getOrDefault(country, "Local Civil Code"));
    }

    // ── Simulation Helpers ───────────────────────────────────

    private String generateContractRef(File file) {
        String prefix = switch (country) {
            case CO -> "CONT-CO";
            case MX -> "CONT-MX";
            case AR -> "CONT-AR";
            case CL -> "CONT-CL";
        };
        int hash = Math.abs(file.getName().hashCode()) % 90000 + 10000;
        return prefix + "-" + hash + "/" + LocalDate.now().getYear();
    }

    private int estimateClauses(long sizeBytes, FileFormat format) {
        long bytesPerClause = switch (format) {
            case PDF -> 5_000L;
            case DOCX, DOC -> 3_500L;
            default -> 2_000L;
        };
        return (int) Math.max(3, sizeBytes / bytesPerClause);
    }

    private int estimatePages(long sizeBytes, FileFormat format) {
        long bytesPerPage = switch (format) {
            case PDF -> 50_000L;
            case DOCX, DOC -> 20_000L;
            default -> 8_000L;
        };
        return (int) Math.max(1, sizeBytes / bytesPerPage);
    }

    /**
     * Simulates detection of signature fields based on filename patterns.
     */
    private String detectSignatureFields(File file) {
        String name = file.getName().toLowerCase();
        if (name.contains("signed") || name.contains("firmado"))
            return "DETECTED (pre-signed)";
        if (name.contains("draft") || name.contains("borrador"))
            return "NOT FOUND (draft)";
        return "PENDING";
    }
}

