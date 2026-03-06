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
 * ============================================================ CONCRETE PRODUCT — Digital
 * Certificate Processor ============================================================ Handles
 * processing of Digital Certificate documents. Instantiated by each country factory via the Factory
 * Method.
 *
 * Processing logic: - Enforces strictest size validation (certs should not be tiny or huge) -
 * Simulates certificate serial number generation - Detects certificate type from filename (SSL,
 * code-signing, personal) - Simulates issuer CA verification per country - Simulates expiry date
 * check - Returns detailed result with certificate metadata
 *
 * Note: All four countries restrict Digital Certificates to PDF only. Chile and Colombia are the
 * most restrictive overall. ============================================================
 */
public class DigitalCertificateProcessor extends BaseDocumentProcessor {

    private static final long MIN_SIZE_BYTES = 1_024L; // 1 KB minimum
    private static final long MAX_SIZE_BYTES = 5_242_880L; // 5 MB maximum (certs should be small)

    // Country-specific Certificate Authorities / issuing bodies
    private static final java.util.Map<CountryCode, String> CERT_AUTHORITY =
            java.util.Map.of(CountryCode.CO, "Certicámara / GTE CyberTrust", CountryCode.MX,
                    "SAT PKI / FIEL", CountryCode.AR, "AFIP — Autoridad Certificante",
                    CountryCode.CL, "E-Certchile / SII CA");

    public DigitalCertificateProcessor(CountryCode country, List<FileFormat> supportedFormats) {
        super(country, supportedFormats);
    }

    /**
     * Core digital certificate processing logic.
     */
    @Override
    protected ProcessingResult doProcess(File file) {
        long fileSize = resolveFileSize(file);

        // Guard: too small — likely not a real certificate
        if (fileSize < MIN_SIZE_BYTES) {
            return failedResult(file,
                    String.format("Certificate '%s' is too small (%d bytes). Minimum: %d bytes.",
                            file.getName(), fileSize, MIN_SIZE_BYTES),
                    ProcessStatus.FAILED);
        }

        // Guard: too large — certificates should be compact documents
        if (fileSize > MAX_SIZE_BYTES) {
            return failedResult(file,
                    String.format(
                            "Certificate '%s' exceeds maximum allowed size (%d MB). "
                                    + "Digital certificates should not exceed 5 MB.",
                            file.getName(), fileSize / 1_048_576),
                    ProcessStatus.FAILED);
        }

        String serial = generateSerialNumber(file);
        String certAuthority = CERT_AUTHORITY.getOrDefault(country, "National CA");
        String certType = detectCertificateType(file);
        LocalDate expiryDate = simulateExpiryDate(file);
        String trustStatus = evaluateTrustChain(file);

        String message = String.format("Digital Certificate processed successfully. "
                + "Serial: %s | CA: %s | Type: %s | "
                + "Expiry: %s | Trust chain: %s | Revocation check: PASSED | Algorithm: SHA-256",
                serial, certAuthority, certType, expiryDate, trustStatus);

        return successResult(file, message);
    }

    @Override
    public DocumentType getDocumentType() {
        return DocumentType.DIGITAL_CERTIFICATE;
    }

    @Override
    public String getDescription() {
        return String.format("Digital Certificate Processor for %s — CA: %s", country.getFullName(),
                CERT_AUTHORITY.getOrDefault(country, "National CA"));
    }

    // ── Simulation Helpers ───────────────────────────────────

    private String generateSerialNumber(File file) {
        long hash = Math.abs((long) file.getName().hashCode()) % 9_000_000_000_000_000L
                + 1_000_000_000_000_000L;
        return String.format("%s-%016X", country.name(), hash);
    }

    /**
     * Infers certificate type from filename keywords.
     */
    private String detectCertificateType(File file) {
        String name = file.getName().toLowerCase();
        if (name.contains("ssl") || name.contains("tls"))
            return "SSL/TLS Certificate";
        if (name.contains("code") || name.contains("sign"))
            return "Code Signing Certificate";
        if (name.contains("root") || name.contains("raiz"))
            return "Root CA Certificate";
        if (name.contains("inter"))
            return "Intermediate CA Certificate";
        if (name.contains("personal") || name.contains("firma"))
            return "Personal Signing Certificate";
        if (name.contains("stamp") || name.contains("sello"))
            return "Electronic Stamp Certificate";
        return "General Purpose Certificate";
    }

    /**
     * Simulates an expiry date — 1–3 years from now based on filename hash.
     */
    private LocalDate simulateExpiryDate(File file) {
        int yearsValid = (Math.abs(file.getName().hashCode()) % 3) + 1;
        return LocalDate.now().plusYears(yearsValid);
    }

    /**
     * Simulates a trust chain evaluation result.
     */
    private String evaluateTrustChain(File file) {
        // Deterministic based on filename — always valid in simulation
        int check = Math.abs(file.getName().hashCode()) % 10;
        return check < 9 ? "VERIFIED" : "REQUIRES_MANUAL_REVIEW";
    }
}

