package test.factory;

import exception.InvalidDocumentForCountryException;
import exception.UnsupportedFileFormatException;
import factory.DocumentProcessorFactory;
import factory.FactoryRegistry;
import factory.ValidationRulesMatrix;
import model.CountryCode;
import model.DocumentType;
import model.FileFormat;
import processor.DocumentProcessor;

/**
 * ============================================================ Manual Validation Tests
 * ============================================================ Verifies country-specific validation
 * rules for all four factories. Run main() to execute all test cases and see pass/fail output.
 * ============================================================
 */
public class ValidationTests {

        private static int passed = 0;
        private static int failed = 0;

        public static void main(String[] args) {
                System.out.println("╔══════════════════════════════════════════════════════════╗");
                System.out.println("║         Country Validation Tests                         ║");
                System.out.println(
                                "╚══════════════════════════════════════════════════════════╝\n");

                testColombiaRules();
                testMexicoRules();
                testArgentinaRules();
                testChileRules();
                testCrossCountryRules();
                printValidationMatrix();
                printSummary();
        }

        // ── Colombia Tests ──────────────────────────────────────

        private static void testColombiaRules() {
                System.out.println("▶ Colombia (CO) Tests");
                System.out.println("  " + "─".repeat(50));

                assertValid(CountryCode.CO, DocumentType.ELECTRONIC_INVOICE, FileFormat.PDF,
                                "CO: Invoice as PDF");
                assertValid(CountryCode.CO, DocumentType.ELECTRONIC_INVOICE, FileFormat.DOCX,
                                "CO: Invoice as DOCX");
                assertValid(CountryCode.CO, DocumentType.LEGAL_CONTRACT, FileFormat.DOC,
                                "CO: Contract as DOC");
                assertValid(CountryCode.CO, DocumentType.FINANCIAL_REPORT, FileFormat.CSV,
                                "CO: Financial Report as CSV");
                assertValid(CountryCode.CO, DocumentType.FINANCIAL_REPORT, FileFormat.TXT,
                                "CO: Financial Report as TXT");
                assertValid(CountryCode.CO, DocumentType.TAX_DECLARATION, FileFormat.XLSX,
                                "CO: Tax Declaration as XLSX");

                assertUnsupportedFormat(CountryCode.CO, DocumentType.ELECTRONIC_INVOICE,
                                FileFormat.CSV, "CO: Invoice as CSV must fail");
                assertUnsupportedFormat(CountryCode.CO, DocumentType.DIGITAL_CERTIFICATE,
                                FileFormat.XLSX, "CO: Digital Cert as XLSX must fail");
                assertUnsupportedFormat(CountryCode.CO, DocumentType.DIGITAL_CERTIFICATE,
                                FileFormat.TXT, "CO: Digital Cert as TXT must fail");
                assertUnsupportedFormat(CountryCode.CO, DocumentType.TAX_DECLARATION, FileFormat.MD,
                                "CO: Tax Declaration as MD must fail");

                System.out.println();
        }

        // ── Mexico Tests ────────────────────────────────────────

        private static void testMexicoRules() {
                System.out.println("▶ Mexico (MX) Tests");
                System.out.println("  " + "─".repeat(50));

                assertValid(CountryCode.MX, DocumentType.ELECTRONIC_INVOICE, FileFormat.PDF,
                                "MX: Invoice as PDF");
                assertValid(CountryCode.MX, DocumentType.ELECTRONIC_INVOICE, FileFormat.XLSX,
                                "MX: Invoice as XLSX");
                assertValid(CountryCode.MX, DocumentType.LEGAL_CONTRACT, FileFormat.DOCX,
                                "MX: Contract as DOCX");
                assertValid(CountryCode.MX, DocumentType.FINANCIAL_REPORT, FileFormat.CSV,
                                "MX: Financial Report as CSV");
                assertValid(CountryCode.MX, DocumentType.TAX_DECLARATION, FileFormat.XLSX,
                                "MX: Tax Declaration as XLSX");

                assertUnsupportedFormat(CountryCode.MX, DocumentType.LEGAL_CONTRACT, FileFormat.TXT,
                                "MX: Contract as TXT must fail");
                assertUnsupportedFormat(CountryCode.MX, DocumentType.LEGAL_CONTRACT, FileFormat.DOC,
                                "MX: Contract as DOC must fail");
                assertUnsupportedFormat(CountryCode.MX, DocumentType.ELECTRONIC_INVOICE,
                                FileFormat.MD, "MX: Invoice as MD must fail");
                assertUnsupportedFormat(CountryCode.MX, DocumentType.TAX_DECLARATION,
                                FileFormat.CSV, "MX: Tax Declaration as CSV must fail");
                assertUnsupportedFormat(CountryCode.MX, DocumentType.DIGITAL_CERTIFICATE,
                                FileFormat.DOCX, "MX: Digital Cert as DOCX must fail");

                System.out.println();
        }

        // ── Argentina Tests ─────────────────────────────────────

        private static void testArgentinaRules() {
                System.out.println("▶ Argentina (AR) Tests");
                System.out.println("  " + "─".repeat(50));

                assertValid(CountryCode.AR, DocumentType.ELECTRONIC_INVOICE, FileFormat.CSV,
                                "AR: Invoice as CSV");
                assertValid(CountryCode.AR, DocumentType.LEGAL_CONTRACT, FileFormat.DOC,
                                "AR: Contract as DOC");
                assertValid(CountryCode.AR, DocumentType.FINANCIAL_REPORT, FileFormat.TXT,
                                "AR: Financial Report as TXT");
                assertValid(CountryCode.AR, DocumentType.DIGITAL_CERTIFICATE, FileFormat.DOCX,
                                "AR: Digital Cert as DOCX");
                assertValid(CountryCode.AR, DocumentType.TAX_DECLARATION, FileFormat.PDF,
                                "AR: Tax Declaration as PDF");

                assertUnsupportedFormat(CountryCode.AR, DocumentType.TAX_DECLARATION,
                                FileFormat.XLSX, "AR: Tax Declaration as XLSX must fail");
                assertUnsupportedFormat(CountryCode.AR, DocumentType.TAX_DECLARATION,
                                FileFormat.DOC, "AR: Tax Declaration as DOC must fail");
                assertUnsupportedFormat(CountryCode.AR, DocumentType.TAX_DECLARATION,
                                FileFormat.CSV, "AR: Tax Declaration as CSV must fail");
                assertUnsupportedFormat(CountryCode.AR, DocumentType.DIGITAL_CERTIFICATE,
                                FileFormat.XLSX, "AR: Digital Cert as XLSX must fail");
                assertUnsupportedFormat(CountryCode.AR, DocumentType.ELECTRONIC_INVOICE,
                                FileFormat.TXT, "AR: Invoice as TXT must fail");

                System.out.println();
        }

        // ── Chile Tests ──────────────────────────────────────────

        private static void testChileRules() {
                System.out.println("▶ Chile (CL) Tests");
                System.out.println("  " + "─".repeat(50));

                assertValid(CountryCode.CL, DocumentType.ELECTRONIC_INVOICE, FileFormat.XLSX,
                                "CL: Invoice as XLSX");
                assertValid(CountryCode.CL, DocumentType.LEGAL_CONTRACT, FileFormat.DOC,
                                "CL: Contract as DOC");
                assertValid(CountryCode.CL, DocumentType.FINANCIAL_REPORT, FileFormat.DOCX,
                                "CL: Financial Report as DOCX");
                assertValid(CountryCode.CL, DocumentType.TAX_DECLARATION, FileFormat.XLSX,
                                "CL: Tax Declaration as XLSX");

                assertUnsupportedFormat(CountryCode.CL, DocumentType.FINANCIAL_REPORT,
                                FileFormat.CSV, "CL: Financial Report as CSV must fail");
                assertUnsupportedFormat(CountryCode.CL, DocumentType.FINANCIAL_REPORT,
                                FileFormat.TXT, "CL: Financial Report as TXT must fail");
                assertUnsupportedFormat(CountryCode.CL, DocumentType.DIGITAL_CERTIFICATE,
                                FileFormat.DOCX, "CL: Digital Cert as DOCX must fail");
                assertUnsupportedFormat(CountryCode.CL, DocumentType.DIGITAL_CERTIFICATE,
                                FileFormat.XLSX, "CL: Digital Cert as XLSX must fail");
                assertUnsupportedFormat(CountryCode.CL, DocumentType.ELECTRONIC_INVOICE,
                                FileFormat.MD, "CL: Invoice as MD must fail");

                System.out.println();
        }

        // ── Cross-Country Comparison Tests ───────────────────────

        private static void testCrossCountryRules() {
                System.out.println("▶ Cross-Country Differential Tests");
                System.out.println("  " + "─".repeat(50));

                assertValid(CountryCode.CO, DocumentType.FINANCIAL_REPORT, FileFormat.CSV,
                                "CO allows CSV for Financial Report");
                assertValid(CountryCode.MX, DocumentType.FINANCIAL_REPORT, FileFormat.CSV,
                                "MX allows CSV for Financial Report");
                assertValid(CountryCode.AR, DocumentType.FINANCIAL_REPORT, FileFormat.CSV,
                                "AR allows CSV for Financial Report");
                assertUnsupportedFormat(CountryCode.CL, DocumentType.FINANCIAL_REPORT,
                                FileFormat.CSV, "CL rejects CSV for Financial Report");

                assertValid(CountryCode.CO, DocumentType.LEGAL_CONTRACT, FileFormat.DOC,
                                "CO allows DOC for Legal Contract");
                assertUnsupportedFormat(CountryCode.MX, DocumentType.LEGAL_CONTRACT, FileFormat.DOC,
                                "MX rejects DOC for Legal Contract");
                assertValid(CountryCode.AR, DocumentType.LEGAL_CONTRACT, FileFormat.DOC,
                                "AR allows DOC for Legal Contract");
                assertValid(CountryCode.CL, DocumentType.LEGAL_CONTRACT, FileFormat.DOC,
                                "CL allows DOC for Legal Contract");

                assertValid(CountryCode.CO, DocumentType.TAX_DECLARATION, FileFormat.XLSX,
                                "CO allows XLSX for Tax Declaration");
                assertValid(CountryCode.MX, DocumentType.TAX_DECLARATION, FileFormat.XLSX,
                                "MX allows XLSX for Tax Declaration");
                assertUnsupportedFormat(CountryCode.AR, DocumentType.TAX_DECLARATION,
                                FileFormat.XLSX, "AR rejects XLSX for Tax Declaration");
                assertValid(CountryCode.CL, DocumentType.TAX_DECLARATION, FileFormat.XLSX,
                                "CL allows XLSX for Tax Declaration");

                System.out.println();
        }

        // ── Validation Matrix ────────────────────────────────────

        private static void printValidationMatrix() {
                ValidationRulesMatrix.printMatrix();
        }

        // ── Test Helpers ─────────────────────────────────────────

        private static void assertValid(CountryCode country, DocumentType docType,
                        FileFormat format, String testName) {
                try {
                        DocumentProcessorFactory factory = FactoryRegistry.getFactory(country);
                        String dummyFile = "test" + format.getExtension();
                        DocumentProcessor processor =
                                        factory.getProcessor(docType, format, dummyFile);
                        pass(testName + " → got: " + processor.getClass().getSimpleName());
                } catch (Exception e) {
                        fail(testName + " → unexpected exception: " + e.getMessage());
                }
        }

        private static void assertUnsupportedFormat(CountryCode country, DocumentType docType,
                        FileFormat format, String testName) {
                try {
                        DocumentProcessorFactory factory = FactoryRegistry.getFactory(country);
                        String dummyFile = "test" + format.getExtension();
                        factory.getProcessor(docType, format, dummyFile);
                        fail(testName + " → expected exception but none was thrown");
                } catch (UnsupportedFileFormatException e) {
                        pass(testName + " → correctly threw: " + e.getMessage());
                } catch (InvalidDocumentForCountryException e) {
                        pass(testName + " → correctly threw: " + e.getMessage());
                } catch (Exception e) {
                        fail(testName + " → wrong exception type: " + e.getClass().getSimpleName()
                                        + ": " + e.getMessage());
                }
        }

        private static void pass(String message) {
                System.out.println("  ✅ PASS | " + message);
                passed++;
        }

        private static void fail(String message) {
                System.out.println("  ❌ FAIL | " + message);
                failed++;
        }

        private static void printSummary() {
                int total = passed + failed;
                System.out.println("╔══════════════════════════════════════════════════════════╗");
                System.out.printf("║  Results: %d/%d passed  |  %d failed%s║%n", passed, total,
                                failed,
                                " ".repeat(Math.max(0, 28 - String.valueOf(total).length())));
                System.out.println("╚══════════════════════════════════════════════════════════╝");
                if (failed == 0) {
                        System.out.println("  validation tests passed!\n");
                } else {
                        System.out.println("  Some tests failed — review rules above.\n");
                }
        }
}
