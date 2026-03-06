package test.batch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import batch.BatchProcessor;
import batch.ErrorHandler;
import exception.DocumentProcessingException;
import exception.InvalidDocumentForCountryException;
import exception.UnsupportedFileFormatException;
import model.BatchItem;
import model.BatchReport;
import model.CountryCode;
import model.DocumentType;
import model.ErrorCategory;
import model.FailureRecord;
import model.FileFormat;

/**
 * ============================================================ Batch Processing & Error Handling
 * Tests ============================================================ Verifies: 1. Full batch with
 * all valid items → all succeed 2. Partial batch with intentional failures → partial report 3.
 * Country rule violations are caught and classified 4. Format violations are caught and classified
 * 5. Empty file failures are caught and classified 6. All-failure batch still returns a report (no
 * exception) 7. Progress callback fires for each item 8. BatchReport fields are correct (counts,
 * rate, summary) 9. ErrorHandler classification accuracy 10. Single-item processSingle()
 * convenience method ============================================================
 */
public class BatchTests {

        private static int passed = 0;
        private static int failed = 0;

        public static void main(String[] args) throws IOException {
                System.out.println("║        — Batch Processing Tests                 ║");

                testAllValidBatch();
                testPartialFailureBatch();
                testCountryRuleViolationsBatch();
                testFormatViolationsBatch();
                testAllFailureBatch();
                testProgressCallback();
                testBatchReportFields();
                testErrorHandlerClassification();
                testProcessSingle();
                testMaxBatchSizeGuard();

                printSummary();
        }

        // ── Test 1: All Valid ─────────────────────────────────────

        private static void testAllValidBatch() throws IOException {
                System.out.println("▶ Test 1 — All valid items → all succeed");
                System.out.println("  " + "─".repeat(54));

                List<BatchItem> items = List.of(
                                item(1, CountryCode.CO, DocumentType.ELECTRONIC_INVOICE,
                                                FileFormat.PDF, "invoice_co.pdf"),
                                item(2, CountryCode.MX, DocumentType.LEGAL_CONTRACT, FileFormat.PDF,
                                                "contract_mx.pdf"),
                                item(3, CountryCode.AR, DocumentType.FINANCIAL_REPORT,
                                                FileFormat.CSV, "report_ar.csv"),
                                item(4, CountryCode.CL, DocumentType.TAX_DECLARATION,
                                                FileFormat.PDF, "tax_cl.pdf"),
                                item(5, CountryCode.CO, DocumentType.DIGITAL_CERTIFICATE,
                                                FileFormat.PDF, "cert_co.pdf"));

                BatchReport report = new BatchProcessor().process(items);

                assertEquals(5, report.getTotalSubmitted(), "Submitted count = 5");
                assertEquals(5, report.getSuccessCount(), "All 5 succeed");
                assertEquals(0, report.getFailureCount(), "No failures");
                assertTrue(report.allSucceeded(), "allSucceeded() is true");
                assertFalse(report.hasFailures(), "hasFailures() is false");
                assertEquals(1.0, report.getSuccessRate(), "Success rate = 100%");
                System.out.println();
        }

        private static void testPartialFailureBatch() throws IOException {
                System.out.println("▶ Test 2 — 3 valid, 2 intentionally invalid → partial report");
                System.out.println("  " + "─".repeat(54));

                List<BatchItem> items = List.of(
                                item(1, CountryCode.CO, DocumentType.ELECTRONIC_INVOICE,
                                                FileFormat.PDF, "valid_invoice.pdf"),
                                item(2, CountryCode.MX, DocumentType.LEGAL_CONTRACT, FileFormat.PDF,
                                                "valid_contract.pdf"),
                                item(3, CountryCode.AR, DocumentType.FINANCIAL_REPORT,
                                                FileFormat.CSV, "valid_report.csv"),
                                // ↓ INVALID: Mexico rejects TXT for Legal Contracts
                                item(4, CountryCode.MX, DocumentType.LEGAL_CONTRACT, FileFormat.TXT,
                                                "bad_contract_mx.txt"),
                                // ↓ INVALID: Chile rejects CSV for Financial Reports
                                item(5, CountryCode.CL, DocumentType.FINANCIAL_REPORT,
                                                FileFormat.CSV, "bad_report_cl.csv"));

                BatchReport report = new BatchProcessor().process(items);

                System.out.println(report.toDisplayString());

                assertEquals(5, report.getTotalSubmitted(), "Submitted = 5");
                assertEquals(3, report.getSuccessCount(), "3 succeed");
                assertEquals(2, report.getFailureCount(), "2 fail");
                assertFalse(report.allSucceeded(), "Not all succeeded");
                assertTrue(report.hasFailures(), "Has failures");

                for (FailureRecord f : report.getFailures()) {
                        assertTrue(f.getErrorCategory() == ErrorCategory.FORMAT,
                                        "Failure is FORMAT category: " + f.getItem().getItemId());
                }
                System.out.println();
        }

        // ── Test 3: Country Rule Violations ──────────────────────

        private static void testCountryRuleViolationsBatch() throws IOException {
                System.out.println("▶ Test 3 — Country rule violations are caught as COUNTRY_RULE");
                System.out.println("  " + "─".repeat(54));

                var countryEx = new InvalidDocumentForCountryException(CountryCode.MX,
                                DocumentType.TAX_DECLARATION, "tax_mx.txt");
                assertEquals(ErrorCategory.COUNTRY_RULE, ErrorHandler.classify(countryEx),
                                "InvalidDocumentForCountryException → COUNTRY_RULE");

                var formatEx = new UnsupportedFileFormatException(CountryCode.CL,
                                DocumentType.FINANCIAL_REPORT, FileFormat.CSV,
                                List.of(FileFormat.PDF, FileFormat.XLSX, FileFormat.DOCX),
                                "report_cl.csv");
                assertEquals(ErrorCategory.FORMAT, ErrorHandler.classify(formatEx),
                                "UnsupportedFileFormatException → FORMAT");

                var procEx = new DocumentProcessingException(
                                "Processing failed due to corrupt content.", "bad_file.pdf", "AR");
                assertEquals(ErrorCategory.PROCESSING, ErrorHandler.classify(procEx),
                                "DocumentProcessingException (content) → PROCESSING");

                var validEx = new DocumentProcessingException("File cannot be null.", "null", "CO");
                assertEquals(ErrorCategory.VALIDATION, ErrorHandler.classify(validEx),
                                "DocumentProcessingException (null) → VALIDATION");

                var sysEx = new RuntimeException("Disk I/O failure");
                assertEquals(ErrorCategory.SYSTEM, ErrorHandler.classify(sysEx),
                                "RuntimeException → SYSTEM");

                System.out.println();
        }

        // ── Test 4: Format Violations Batch ──────────────────────

        private static void testFormatViolationsBatch() throws IOException {
                System.out.println(
                                "▶ Test 4 — All FORMAT violations → zero successes, all FORMAT failures");
                System.out.println("  " + "─".repeat(54));

                List<BatchItem> items = List.of(
                                item(1, CountryCode.CO, DocumentType.ELECTRONIC_INVOICE,
                                                FileFormat.CSV, "invoice.csv"),
                                item(2, CountryCode.MX, DocumentType.LEGAL_CONTRACT, FileFormat.TXT,
                                                "contract.txt"),
                                item(3, CountryCode.AR, DocumentType.TAX_DECLARATION,
                                                FileFormat.XLSX, "tax.xlsx"),
                                item(4, CountryCode.CL, DocumentType.FINANCIAL_REPORT,
                                                FileFormat.CSV, "report.csv"),
                                item(5, CountryCode.CL, DocumentType.DIGITAL_CERTIFICATE,
                                                FileFormat.DOCX, "cert.docx"));

                BatchReport report = new BatchProcessor().process(items);

                assertEquals(0, report.getSuccessCount(), "Zero successes");
                assertEquals(5, report.getFailureCount(), "All 5 fail");
                assertTrue(report.allFailed(), "allFailed() is true");

                long formatFailures = report.getFailures().stream()
                                .filter(f -> f.getErrorCategory() == ErrorCategory.FORMAT).count();
                assertEquals(5L, formatFailures, "All 5 failures are FORMAT category");
                System.out.println();
        }

        // ── Test 5: All-Failure Batch ─────────────────────────────

        private static void testAllFailureBatch() throws IOException {
                System.out.println(
                                "▶ Test 5 — Mixed failures → BatchReport still returned (no exception)");
                System.out.println("  " + "─".repeat(54));

                List<BatchItem> items = new ArrayList<>();
                items.add(item(1, CountryCode.MX, DocumentType.LEGAL_CONTRACT, FileFormat.TXT,
                                "bad1.txt"));
                items.add(item(2, CountryCode.CL, DocumentType.FINANCIAL_REPORT, FileFormat.CSV,
                                "bad2.csv"));

                File emptyFile = File.createTempFile("empty_batch_test", ".pdf");
                emptyFile.deleteOnExit();
                items.add(new BatchItem(3, CountryCode.CO, DocumentType.ELECTRONIC_INVOICE,
                                FileFormat.PDF, emptyFile));

                BatchReport report = new BatchProcessor().process(items);

                assertNotNull(report, "BatchReport is never null");
                assertNotNull(report.getBatchId(), "BatchId is set");
                assertTrue(report.getFailureCount() >= 2, "At least 2 failures");
                assertNotNull(report.summary(), "summary() returns a string");
                System.out.println("  Report: " + report.summary());
                System.out.println();
        }

        private static void testProgressCallback() throws IOException {
                System.out.println("▶ Test 6 — Progress callback fires for each item");
                System.out.println("  " + "─".repeat(54));

                List<String> log = new ArrayList<>();

                List<BatchItem> items = List.of(
                                item(1, CountryCode.CO, DocumentType.ELECTRONIC_INVOICE,
                                                FileFormat.PDF, "inv1.pdf"),
                                item(2, CountryCode.MX, DocumentType.LEGAL_CONTRACT, FileFormat.PDF,
                                                "con2.pdf"));

                new BatchProcessor().onProgress(log::add).process(items);

                assertTrue(!log.isEmpty(), "Progress log is not empty");
                assertTrue(log.size() >= 2, "At least one log entry per item");
                System.out.println("  Progress lines captured: " + log.size());
                log.forEach(l -> System.out.println("    » " + l));
                System.out.println();
        }

        private static void testBatchReportFields() throws IOException {
                System.out.println("▶ Test 7 — BatchReport fields are fully populated");
                System.out.println("  " + "─".repeat(54));

                BatchReport report = new BatchProcessor().process(List.of(item(1, CountryCode.CO,
                                DocumentType.ELECTRONIC_INVOICE, FileFormat.PDF, "invoice.pdf")));

                assertNotNull(report.getBatchId(), "batchId is set");
                assertNotNull(report.getStartedAt(), "startedAt is set");
                assertNotNull(report.getCompletedAt(), "completedAt is set");
                assertNotNull(report.getSuccesses(), "successes list is set");
                assertNotNull(report.getFailures(), "failures list is set");
                assertTrue(report.getDurationMs() >= 0, "durationMs >= 0");
                assertEquals(1, report.getTotalSubmitted(), "totalSubmitted = 1");
                assertTrue(report.getBatchId().startsWith("BATCH-"), "batchId starts with BATCH-");
                System.out.println();
        }

        private static void testErrorHandlerClassification() {
                System.out.println(" Test 8 — ErrorHandler.classify() accuracy");
                System.out.println("  " + "─".repeat(54));

                assertEquals(ErrorCategory.COUNTRY_RULE,
                                ErrorHandler.classify(new InvalidDocumentForCountryException(
                                                CountryCode.AR, DocumentType.TAX_DECLARATION,
                                                "tax.doc")),
                                "InvalidDocumentForCountry → COUNTRY_RULE");
                assertEquals(ErrorCategory.FORMAT,
                                ErrorHandler.classify(new UnsupportedFileFormatException(
                                                CountryCode.CL, DocumentType.FINANCIAL_REPORT,
                                                FileFormat.CSV,
                                                List.of(FileFormat.PDF, FileFormat.XLSX),
                                                "fin.csv")),
                                "UnsupportedFileFormat → FORMAT");
                assertEquals(ErrorCategory.SYSTEM,
                                ErrorHandler.classify(new NullPointerException("unexpected NPE")),
                                "NullPointerException → SYSTEM");
                assertEquals(ErrorCategory.VALIDATION,
                                ErrorHandler.classify(
                                                new IllegalArgumentException("field missing")),
                                "IllegalArgumentException → VALIDATION");

                System.out.println();
        }

        private static void testProcessSingle() throws IOException {
                System.out.println("▶ Test 9 — processSingle() works like a one-item batch");
                System.out.println("  " + "─".repeat(54));

                BatchItem single = item(1, CountryCode.MX, DocumentType.ELECTRONIC_INVOICE,
                                FileFormat.PDF, "single.pdf");
                BatchReport report = new BatchProcessor().processSingle(single);

                assertEquals(1, report.getTotalSubmitted(), "processSingle: submitted = 1");
                assertEquals(1, report.getSuccessCount(), "processSingle: success = 1");
                assertEquals(0, report.getFailureCount(), "processSingle: failures = 0");
                System.out.println();
        }

        private static void testMaxBatchSizeGuard() throws IOException {
                System.out.println(" Test 10 — maxBatchSize guard throws when exceeded");
                System.out.println("  " + "─".repeat(54));

                BatchProcessor limited = new BatchProcessor(2);
                List<BatchItem> oversized = List.of(
                                item(1, CountryCode.CO, DocumentType.ELECTRONIC_INVOICE,
                                                FileFormat.PDF, "a.pdf"),
                                item(2, CountryCode.CO, DocumentType.ELECTRONIC_INVOICE,
                                                FileFormat.PDF, "b.pdf"),
                                item(3, CountryCode.CO, DocumentType.ELECTRONIC_INVOICE,
                                                FileFormat.PDF, "c.pdf") // exceeds
                                                                         // limit
                );

                try {
                        limited.process(oversized);
                        fail("Expected IllegalArgumentException for oversized batch");
                } catch (IllegalArgumentException e) {
                        pass("Correctly threw IllegalArgumentException: " + e.getMessage());
                }
                System.out.println();
        }

        private static BatchItem item(int index, CountryCode country, DocumentType type,
                        FileFormat format, String filename) {
                return new BatchItem(index, country, type, format, new File(filename));
        }

        private static void assertTrue(boolean condition, String label) {
                if (condition)
                        pass(label);
                else
                        fail(label + " → condition was false");
        }

        private static void assertFalse(boolean condition, String label) {
                if (!condition)
                        pass(label);
                else
                        fail(label + " → condition was true");
        }

        private static void assertEquals(Object expected, Object actual, String label) {
                if (expected.equals(actual))
                        pass(label + " → " + actual);
                else
                        fail(label + " → expected [" + expected + "] but got [" + actual + "]");
        }

        private static void assertNotNull(Object value, String label) {
                if (value != null)
                        pass(label);
                else
                        fail(label + " is NULL");
        }

        private static void pass(String msg) {
                System.out.println("   " + msg);
                passed++;
        }

        private static void fail(String msg) {
                System.out.println("  ❌ " + msg);
                failed++;
        }

        private static void printSummary() {
                int total = passed + failed;
                System.out.println("╔══════════════════════════════════════════════════════════╗");
                System.out.printf("║  Results: %d/%d passed  |  %d failed%s║%n", passed, total,
                                failed,
                                " ".repeat(Math.max(0, 28 - String.valueOf(total).length())));
                System.out.println("╚══════════════════════════════════════════════════════════╝");
                if (failed == 0)
                        System.out.println("🎉  batch tests passed!\n");
                else
                        System.out.println("⚠️  Some tests failed — review output above.\n");
        }
}
