package test.processor;

import java.io.File;
import factory.DocumentProcessorFactory;
import factory.FactoryRegistry;
import model.CountryCode;
import model.DocumentType;
import model.FileFormat;
import model.ProcessStatus;
import model.ProcessingResult;
import processor.DocumentProcessor;

/**
 * ============================================================ Processor Business Logic Tests
 * ============================================================
 */
public class ProcessorTests {

        private static int passed = 0;
        private static int failed = 0;

        public static void main(String[] args) {
                testAllProcessorsSuccessPath();
                testEmptyFileFailPath();
                testResultFieldsArePopulated(); // Fixed syntax error here
                testCrossCountryConsistency();
                printSummary();
        }

        private static void testAllProcessorsSuccessPath() {
                // CO — Colombia
                processAndExpectSuccess(CountryCode.CO, DocumentType.ELECTRONIC_INVOICE,
                                FileFormat.PDF, "invoice_co.pdf");
                processAndExpectSuccess(CountryCode.CO, DocumentType.LEGAL_CONTRACT,
                                FileFormat.DOCX, "contrato_co.docx");

                // MX — Mexico
                processAndExpectSuccess(CountryCode.MX, DocumentType.ELECTRONIC_INVOICE,
                                FileFormat.XLSX, "cfdi_mx.xlsx");

                // AR — Argentina
                processAndExpectSuccess(CountryCode.AR, DocumentType.ELECTRONIC_INVOICE,
                                FileFormat.CSV, "factura_ar.csv");

                // CL — Chile
                processAndExpectSuccess(CountryCode.CL, DocumentType.ELECTRONIC_INVOICE,
                                FileFormat.XLSX, "dte_cl.xlsx");
        }

        private static void testEmptyFileFailPath() {
                processAndExpectFailed(CountryCode.CO, DocumentType.ELECTRONIC_INVOICE,
                                FileFormat.PDF, "empty.pdf");
        }

        private static void testResultFieldsArePopulated() {
                try {
                        DocumentProcessorFactory factory =
                                        FactoryRegistry.getFactory(CountryCode.CO);
                        DocumentProcessor processor =
                                        factory.getProcessor(DocumentType.ELECTRONIC_INVOICE,
                                                        FileFormat.PDF, "test_invoice.pdf");

                        ProcessingResult result = processor.process(new File("test_invoice.pdf"));

                        assertNotNull(result.getDocumentId(), "documentId");
                        assertNotNull(result.getDocumentName(), "documentName");
                        assertNotNull(result.getCountry(), "country");
                        assertCondition(result.getFileSizeBytes() > 0, "fileSizeBytes > 0");
                } catch (Exception e) {
                        fail("Result fields test failed: " + e.getMessage());
                }
        }

        private static void testCrossCountryConsistency() {
                for (CountryCode c : CountryCode.values()) {
                        processAndExpectSuccess(c, DocumentType.ELECTRONIC_INVOICE, FileFormat.PDF,
                                        "inv_" + c.name() + ".pdf");
                }
        }

        private static void processAndExpectSuccess(CountryCode country, DocumentType docType,
                        FileFormat format, String filename) {
                String label = country.name() + " | " + docType.getDisplayName();
                try {
                        DocumentProcessorFactory factory = FactoryRegistry.getFactory(country);
                        DocumentProcessor processor =
                                        factory.getProcessor(docType, format, filename);
                        ProcessingResult result = processor.process(new File(filename));

                        if (result.getStatus() == ProcessStatus.SUCCESS) {
                                pass(label + " ✓");
                        } else {
                                fail(label + " -> Expected SUCCESS, got " + result.getStatus());
                        }
                } catch (Exception e) {
                        fail(label + " -> Exception: " + e.getMessage());
                }
        }

        private static void processAndExpectFailed(CountryCode country, DocumentType docType,
                        FileFormat format, String filename) {
                try {
                        DocumentProcessorFactory factory = FactoryRegistry.getFactory(country);
                        DocumentProcessor processor =
                                        factory.getProcessor(docType, format, filename);
                        File emptyFile = File.createTempFile("empty", format.getExtension());
                        emptyFile.deleteOnExit();

                        ProcessingResult result = processor.process(emptyFile);

                        if (result.getStatus() == ProcessStatus.FAILED) {
                                pass(country.name() + " Empty File -> Correctly FAILED");
                        } else {
                                fail(country.name() + " Empty File -> Expected FAILED");
                        }
                } catch (Exception e) {
                        fail("Exception in fail path: " + e.getMessage());
                }
        }

        private static void assertNotNull(Object value, String label) {
                if (value != null)
                        pass("Field: " + label);
                else
                        fail("Field: " + label + " is NULL");
        }

        private static void assertCondition(boolean condition, String label) {
                if (condition)
                        pass("Condition: " + label);
                else
                        fail("Condition FAILED: " + label);
        }

        private static void pass(String msg) {
                passed++;
        }

        private static void fail(String msg) {
                failed++;
                System.out.println("  [FAIL] " + msg);
        }

        private static void printSummary() {
                System.out.printf("\nResults: %d Passed | %d Failed\n", passed, failed);
        }
}
