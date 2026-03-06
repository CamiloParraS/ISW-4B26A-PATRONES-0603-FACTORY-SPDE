package batch;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import factory.DocumentProcessorFactory;
import factory.FactoryRegistry;
import model.BatchItem;
import model.BatchReport;
import model.ErrorCategory;
import model.FailureRecord;
import model.ProcessingResult;
import processor.DocumentProcessor;

public class BatchProcessor {

    private Consumer<String> progressCallback;
    private final int maxBatchSize;


    public BatchProcessor() {
        this.maxBatchSize = 0;
    }

    public BatchProcessor(int maxBatchSize) {
        if (maxBatchSize < 0)
            throw new IllegalArgumentException("maxBatchSize must be >= 0");
        this.maxBatchSize = maxBatchSize;
    }

    public BatchProcessor onProgress(Consumer<String> callback) {
        this.progressCallback = callback;
        return this; // fluent
    }

    public BatchReport process(List<BatchItem> items) {
        if (items == null) {
            throw new IllegalArgumentException("Batch item list cannot be null.");
        }
        if (maxBatchSize > 0 && items.size() > maxBatchSize) {
            throw new IllegalArgumentException(String.format(
                    "Batch size %d exceeds maximum allowed: %d", items.size(), maxBatchSize));
        }

        String batchId = "BATCH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        LocalDateTime startedAt = LocalDateTime.now();
        long startMs = System.currentTimeMillis();

        List<ProcessingResult> successes = new ArrayList<>();
        List<FailureRecord> failures = new ArrayList<>();

        notifyProgress(
                String.format("Batch %s started — %d item(s) queued.", batchId, items.size()));

        for (int i = 0; i < items.size(); i++) {
            BatchItem item = items.get(i);
            String progress =
                    String.format("[%d/%d] Processing: %s", i + 1, items.size(), item.getLabel());
            notifyProgress(progress);

            try {
                DocumentProcessorFactory factory = FactoryRegistry.getFactory(item.getCountry());

                DocumentProcessor processor = factory.getProcessor(item.getDocumentType(),
                        item.getFileFormat(), item.getFile().getName());

                ProcessingResult result = processor.process(item.getFile());

                if (result.isSuccess()) {
                    successes.add(result);
                    notifyProgress("  SUCCESS: " + result.getMessage().substring(0,
                            Math.min(80, result.getMessage().length())) + "...");
                } else {
                    FailureRecord failureRecord = new FailureRecord(item, ErrorCategory.PROCESSING,
                            result.getMessage(), "ProcessorReturnedFailure");
                    failures.add(failureRecord);
                    notifyProgress("  FAILED (processor): " + result.getMessage());
                }

            } catch (Exception ex) {
                FailureRecord failureRecord = ErrorHandler.handle(item, ex);
                failures.add(failureRecord);
                notifyProgress("  ✖ ERROR (" + failureRecord.getErrorCategory().getLabel() + "): "
                        + failureRecord.getErrorMessage().substring(0,
                                Math.min(100, failureRecord.getErrorMessage().length())));
            }
        }

        long durationMs = System.currentTimeMillis() - startMs;
        LocalDateTime completedAt = LocalDateTime.now();

        BatchReport report = new BatchReport(batchId, items.size(), successes, failures, startedAt,
                completedAt, durationMs);

        notifyProgress(String.format("■ Batch %s complete. %s", batchId, report.summary()));
        return report;
    }

    public BatchReport processSingle(BatchItem item) {
        return process(List.of(item));
    }


    private void notifyProgress(String message) {
        if (progressCallback != null) {
            progressCallback.accept(message);
        }
    }
}
