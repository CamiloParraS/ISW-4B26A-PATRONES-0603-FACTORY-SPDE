package processor;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import model.CountryCode;
import model.DocumentType;
import model.FileFormat;
import model.ProcessStatus;
import model.ProcessingResult;

public class TaxDeclarationProcessor extends BaseDocumentProcessor {

    private static final long MIN_SIZE_BYTES = 512L;

    private static final java.util.Map<CountryCode, String> TAX_FORM_CODE =
            java.util.Map.of(CountryCode.CO, "Formulario 110/210 (DIAN)", CountryCode.MX,
                    "Declaración Anual SAT / DIOT", CountryCode.AR, "F. 711 / AFIP SIFERE",
                    CountryCode.CL, "Formulario 22 / F29 (SII)");

    private static final java.util.Map<CountryCode, String> TAX_AUTHORITY =
            java.util.Map.of(CountryCode.CO, "DIAN", CountryCode.MX, "SAT", CountryCode.AR, "AFIP",
                    CountryCode.CL, "SII");

    public TaxDeclarationProcessor(CountryCode country, List<FileFormat> supportedFormats) {
        super(country, supportedFormats);
    }

    @Override
    protected ProcessingResult doProcess(File file) {
        long fileSize = resolveFileSize(file);

        if (fileSize < MIN_SIZE_BYTES) {
            return failedResult(file,
                    String.format(
                            "Tax declaration '%s' appears empty (%d bytes). "
                                    + "A valid declaration requires at least %d bytes.",
                            file.getName(), fileSize, MIN_SIZE_BYTES),
                    ProcessStatus.FAILED);
        }

        FileFormat format = resolveFormat(file);
        String formCode = TAX_FORM_CODE.getOrDefault(country, "National Tax Form");
        String authority = TAX_AUTHORITY.getOrDefault(country, "Tax Authority");
        String taxPeriod = detectTaxPeriod(file);
        String declarationType = detectDeclarationType(file);
        String submissionRef = generateSubmissionRef(file);
        String deadlineStatus = evaluateDeadlineStatus(file);

        String message = String.format(
                "Tax Declaration processed successfully. "
                        + "Submission ref: %s | Authority: %s | Form: %s | "
                        + "Period: %s | Type: %s | Deadline status: %s | "
                        + "Digital signature: REQUIRED | Format: %s",
                submissionRef, authority, formCode, taxPeriod, declarationType, deadlineStatus,
                format.getExtension());

        return successResult(file, message);
    }

    @Override
    public DocumentType getDocumentType() {
        return DocumentType.TAX_DECLARATION;
    }

    @Override
    public String getDescription() {
        return String.format("Tax Declaration Processor for %s — form: %s", country.getFullName(),
                TAX_FORM_CODE.getOrDefault(country, "National Tax Form"));
    }


    private String generateSubmissionRef(File file) {
        String authority = TAX_AUTHORITY.getOrDefault(country, "TX");
        int hash = (Math.abs(file.getName().hashCode()) % 900_000) + 100_000;
        return authority + "-" + LocalDate.now().getYear() + "-" + hash;
    }

    private String detectTaxPeriod(File file) {
        String name = file.getName().toLowerCase();
        if (name.contains("enero") || name.contains("jan"))
            return ("January " + LocalDate.now().getYear());
        if (name.contains("febrero") || name.contains("feb"))
            return ("February " + LocalDate.now().getYear());
        if (name.contains("marzo") || name.contains("mar"))
            return ("March " + LocalDate.now().getYear());
        if (name.contains("annual") || name.contains("anual"))
            return ("Annual " + LocalDate.now().getYear());
        if (name.contains("iva") || name.contains("vat"))
            return ("VAT Period " + LocalDate.now().getYear());
        // Default: current year annual declaration
        return "Fiscal Year " + LocalDate.now().getYear();
    }


    private String detectDeclarationType(File file) {
        String name = file.getName().toLowerCase();
        if (name.contains("iva") || name.contains("vat"))
            return "VAT / IVA Declaration";
        if (name.contains("renta") || name.contains("income"))
            return "Income Tax Declaration";
        if (name.contains("nomina") || name.contains("payroll"))
            return "Payroll Tax Declaration";
        if (name.contains("corp") || name.contains("empresa"))
            return "Corporate Tax Declaration";
        if (name.contains("bienes") || name.contains("assets"))
            return "Asset Tax Declaration";
        return "General Tax Declaration";
    }


    private String evaluateDeadlineStatus(File file) {
        LocalDate deadline = LocalDate.of(LocalDate.now().getYear(), 4, 30);
        LocalDate today = LocalDate.now();
        if (today.isBefore(deadline))
            return "ON TIME (due: " + deadline + ")";
        if (today.isEqual(deadline))
            return "DUE TODAY — " + deadline;
        return ("LATE FILING — deadline was " + deadline + " ("
                + java.time.temporal.ChronoUnit.DAYS.between(deadline, today) + " days overdue)");
    }
}
