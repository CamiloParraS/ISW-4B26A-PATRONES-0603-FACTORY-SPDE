package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import batch.BatchProcessor;
import factory.ValidationRulesMatrix;
import model.BatchItem;
import model.BatchReport;
import model.CountryCode;
import model.DocumentType;
import model.FailureRecord;
import model.FileFormat;
import model.ProcessingResult;

public class DocumentUI extends JFrame {

    private static final Color BG_DARK = new Color(240, 240, 240); // Light Grey (Classic window
                                                                   // background)
    private static final Color BG_PANEL = new Color(255, 255, 255); // Pure White
    private static final Color BG_CARD = new Color(248, 249, 250); // Very Light Grey for subtle
                                                                   // depth
    private static final Color BG_INPUT = new Color(255, 255, 255); // Standard white input field

    private static final Color ACCENT = new Color(0, 120, 215); // Standard Windows Blue (#0078d7)
    private static final Color ACCENT_HOVER = new Color(0, 90, 158); // Darker Blue for hover states

    private static final Color SUCCESS = new Color(25, 135, 84); // Slightly deeper green for
                                                                 // readability on white
    private static final Color DANGER = new Color(220, 53, 69); // Standard Red
    private static final Color WARNING = new Color(255, 193, 7); // Standard Amber

    private static final Color TEXT_PRIMARY = new Color(33, 37, 41); // Near Black (#212529)
    private static final Color TEXT_MUTED = new Color(108, 117, 125); // Slate Grey for subtext
    private static final Color BORDER_CLR = new Color(218, 220, 224); // Soft grey border

    private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 15);
    private static final Font FONT_LABEL = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 11);
    private static final Font FONT_MONO = new Font(Font.MONOSPACED, Font.PLAIN, 11);
    private static final Font FONT_BUTTON = new Font("SansSerif", Font.BOLD, 12);

    private JComboBox<CountryCode> cmbCountry;
    private JComboBox<DocumentType> cmbDocType;
    private JComboBox<FileFormat> cmbFormat;
    private JTextField txtFilePath;
    private JButton btnBrowse;
    private JButton btnProcessSingle;
    private JButton btnAddToBatch;

    private final List<BatchItem> batchQueue = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JTable batchTable;
    private JLabel lblQueueCount;
    private JButton btnProcessBatch;
    private JButton btnClearBatch;
    private JButton btnRemoveItem;

    private JTextArea txtResults;
    private JLabel lblResultsHeader;
    private JButton btnClearResults;

    private JLabel lblStatus;

    private int batchItemCounter = 1;


    public DocumentUI() {
        super("SPDE — Enterprise Document Processing System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 780);
        setMinimumSize(new Dimension(1100, 680));
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        buildUI();
        refreshDocTypeDropdown();
        refreshFormatDropdown();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_DARK);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildMainArea(), BorderLayout.CENTER);

        setContentPane(root);
    }


    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_PANEL);
        header.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_CLR));
        header.setPreferredSize(new Dimension(0, 56));

        JLabel title = new JLabel("   SPDE — Enterprise Document Processing System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(TEXT_PRIMARY);

        header.add(title, BorderLayout.WEST);
        return header;
    }


    private JPanel buildMainArea() {
        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(BG_DARK);
        main.setBorder(new EmptyBorder(12, 12, 12, 12));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 10);
        gbc.weighty = 1.0;

        // LEFT — Form (fixed width)
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.ipadx = 260;
        main.add(buildFormPanel(), gbc);

        // CENTER — Batch queue
        gbc.gridx = 1;
        gbc.weightx = 0.38;
        gbc.ipadx = 0;
        gbc.insets = new Insets(0, 0, 0, 10);
        main.add(buildBatchPanel(), gbc);

        // RIGHT — Results
        gbc.gridx = 2;
        gbc.weightx = 0.62;
        gbc.insets = new Insets(0, 0, 0, 0);
        main.add(buildResultsPanel(), gbc);

        return main;
    }


    private JPanel buildFormPanel() {
        JPanel card = createCard("  Document Configuration");
        card.setPreferredSize(new Dimension(280, 0));
        card.setMinimumSize(new Dimension(260, 0));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // Country
        cmbCountry = createCombo(CountryCode.values());
        cmbCountry.addActionListener(e -> {
            refreshDocTypeDropdown();
            refreshFormatDropdown();
        });

        // Document type
        cmbDocType = new JComboBox<>();
        styleInput(cmbDocType);
        cmbDocType.addActionListener(e -> refreshFormatDropdown());

        // Format
        cmbFormat = new JComboBox<>();
        styleInput(cmbFormat);

        // File path
        txtFilePath = createTextField("No file selected...");
        txtFilePath.setEditable(false);

        btnBrowse = createSecondaryButton("Browse…");
        btnBrowse.addActionListener(this::onBrowse);

        content.add(Box.createVerticalStrut(4));
        content.add(formRow("Country", cmbCountry));
        content.add(Box.createVerticalStrut(8));
        content.add(formRow("Document Type", cmbDocType));
        content.add(Box.createVerticalStrut(8));
        content.add(formRow("File Format", cmbFormat));
        content.add(Box.createVerticalStrut(12));
        content.add(sectionDivider("File Selection"));
        content.add(Box.createVerticalStrut(8));
        content.add(fileRow());
        content.add(Box.createVerticalStrut(16));
        content.add(sectionDivider("Actions"));
        content.add(Box.createVerticalStrut(8));
        content.add(buildActionButtons());
        content.add(Box.createVerticalGlue());

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel formRow(String labelText, JComponent input) {
        JPanel row = new JPanel(new BorderLayout(0, 4));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(TEXT_MUTED);
        styleInput(input);
        row.add(lbl, BorderLayout.NORTH);
        row.add(input, BorderLayout.CENTER);
        row.setBorder(new EmptyBorder(0, 0, 0, 0));
        return row;
    }

    private JPanel fileRow() {
        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        JLabel lbl = new JLabel("Selected File");
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(TEXT_MUTED);
        styleInput(txtFilePath);
        row.add(lbl, BorderLayout.NORTH);
        JPanel inner = new JPanel(new BorderLayout(6, 0));
        inner.setOpaque(false);
        inner.add(txtFilePath, BorderLayout.CENTER);
        inner.add(btnBrowse, BorderLayout.EAST);
        row.add(inner, BorderLayout.CENTER);
        return row;
    }

    private JPanel buildActionButtons() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 8));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 88));

        btnProcessSingle = createPrimaryButton("  Process Single");
        btnProcessSingle
                .setToolTipText("Run factory + processor immediately for the selected file");
        btnProcessSingle.addActionListener(this::onProcessSingle);

        btnAddToBatch = createSecondaryButton("  Add to Batch Queue");
        btnAddToBatch.setToolTipText("Queue this item for batch processing");
        btnAddToBatch.addActionListener(this::onAddToBatch);

        panel.add(btnProcessSingle);
        panel.add(btnAddToBatch);
        return panel;
    }


    // ── CENTER: Batch panel
    private JPanel buildBatchPanel() {
        JPanel card = createCard("  Batch Queue");
        card.setLayout(new BorderLayout(0, 10));

        // Table
        String[] cols = {"ID", "Country", "Type", "Format", "File"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        batchTable = new JTable(tableModel);
        styleTable(batchTable);

        JScrollPane scroll = new JScrollPane(batchTable);
        scroll.setBackground(BG_INPUT);
        scroll.getViewport().setBackground(BG_INPUT);
        scroll.setBorder(new LineBorder(BORDER_CLR));

        // Queue count badge
        lblQueueCount = new JLabel("0 items queued");
        lblQueueCount.setFont(FONT_SMALL);
        lblQueueCount.setForeground(TEXT_MUTED);

        // Batch action buttons row
        JPanel btnRow = new JPanel(new GridLayout(1, 3, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setPreferredSize(new Dimension(0, 36));

        btnProcessBatch = createPrimaryButton("  Run Batch");
        btnProcessBatch.addActionListener(this::onProcessBatch);

        btnRemoveItem = createSecondaryButton("  Remove");
        btnRemoveItem.addActionListener(this::onRemoveItem);

        btnClearBatch = createDangerButton("  Clear All");
        btnClearBatch.addActionListener(this::onClearBatch);

        btnRow.add(btnProcessBatch);
        btnRow.add(btnRemoveItem);
        btnRow.add(btnClearBatch);

        card.add(scroll, BorderLayout.CENTER);
        card.add(lblQueueCount, BorderLayout.NORTH);
        card.add(btnRow, BorderLayout.SOUTH);

        return card;
    }


    private JPanel buildResultsPanel() {
        JPanel card = createCard("  Processing Results");
        card.setLayout(new BorderLayout(0, 8));

        txtResults = new JTextArea();
        txtResults.setEditable(false);
        txtResults.setFont(FONT_MONO);
        txtResults.setBackground(BG_INPUT);
        txtResults.setForeground(TEXT_PRIMARY);
        txtResults.setCaretColor(TEXT_PRIMARY);
        txtResults.setLineWrap(true);
        txtResults.setWrapStyleWord(true);
        txtResults.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(txtResults);
        scroll.setBorder(new LineBorder(BORDER_CLR));
        scroll.getViewport().setBackground(BG_INPUT);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        lblResultsHeader = new JLabel("Ready");
        lblResultsHeader.setFont(FONT_SMALL);
        lblResultsHeader.setForeground(TEXT_MUTED);
        btnClearResults = createSecondaryButton("Clear");
        btnClearResults.setFont(FONT_SMALL);
        btnClearResults.setPreferredSize(new Dimension(70, 24));
        btnClearResults.addActionListener(e -> {
            txtResults.setText("");
            lblResultsHeader.setText("Cleared");
        });
        topRow.add(lblResultsHeader, BorderLayout.WEST);
        topRow.add(btnClearResults, BorderLayout.EAST);

        card.add(topRow, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private void refreshDocTypeDropdown() {
        CountryCode country = getSelectedCountry();
        if (country == null)
            return;
        DocumentType prev = (DocumentType) cmbDocType.getSelectedItem();
        cmbDocType.removeAllItems();
        for (DocumentType dt : DocumentType.values()) {
            cmbDocType.addItem(dt);
        }
        if (prev != null)
            cmbDocType.setSelectedItem(prev);
        refreshFormatDropdown();
    }

    private void refreshFormatDropdown() {
        CountryCode country = getSelectedCountry();
        DocumentType docType = getSelectedDocType();
        if (country == null || docType == null)
            return;

        List<FileFormat> allowed = ValidationRulesMatrix.getAllowedFormats(country, docType);
        FileFormat prev = (FileFormat) cmbFormat.getSelectedItem();
        cmbFormat.removeAllItems();
        for (FileFormat f : allowed)
            cmbFormat.addItem(f);
        if (prev != null && allowed.contains(prev))
            cmbFormat.setSelectedItem(prev);
        else if (!allowed.isEmpty())
            cmbFormat.setSelectedIndex(0);
    }


    private void onBrowse(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Document");
        chooser.setFileFilter(new FileNameExtensionFilter(
                "Supported Documents (pdf, doc, docx, md, csv, txt, xlsx)", "pdf", "doc", "docx",
                "md", "csv", "txt", "xlsx"));
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            txtFilePath.setText(file.getAbsolutePath());

            try {
                FileFormat detected = FileFormat.fromFilename(file.getName());
                cmbFormat.setSelectedItem(detected);
            } catch (IllegalArgumentException ignored) {
            }

            setStatus("File selected: " + file.getName(), TEXT_MUTED);
        }
    }


    private void onProcessSingle(ActionEvent e) {
        BatchItem item = buildItemFromForm("SINGLE");
        if (item == null)
            return;

        setStatus("Processing: " + item.getFile().getName() + "…", ACCENT);
        appendResult("  PROCESS SINGLE — " + now());

        BatchProcessor processor = new BatchProcessor().onProgress(this::appendResult);
        BatchReport report = processor.processSingle(item);

        renderBatchReport(report);
        setStatus(report.summary(), report.hasFailures() ? DANGER : SUCCESS);
    }

    private void onAddToBatch(ActionEvent e) {
        BatchItem item = buildItemFromForm(String.format("%03d", batchItemCounter));
        if (item == null)
            return;

        batchQueue.add(item);
        tableModel.addRow(new Object[] {item.getItemId(), item.getCountry().name(),
                item.getDocumentType().getDisplayName(), item.getFileFormat().getExtension(),
                item.getFile().getName()});
        batchItemCounter++;
        updateQueueCount();
        setStatus("Added to queue: " + item.getFile().getName(), ACCENT);
    }

    private void onProcessBatch(ActionEvent e) {
        if (batchQueue.isEmpty()) {
            showWarning("Batch queue is empty. Add at least one item before running.");
            return;
        }

        setStatus("Running batch — " + batchQueue.size() + " item(s)…", ACCENT);
        appendResult("  BATCH RUN — " + batchQueue.size() + " item(s) — " + now());

        List<BatchItem> snapshot = new ArrayList<>(batchQueue);
        BatchProcessor processor = new BatchProcessor().onProgress(this::appendResult);
        BatchReport report = processor.process(snapshot);

        appendResult("");
        appendResult(report.toDisplayString());
        lblResultsHeader.setText("Batch complete — " + report.summary());
        setStatus(report.summary(), report.hasFailures() ? WARNING : SUCCESS);
    }

    private void onRemoveItem(ActionEvent e) {
        int row = batchTable.getSelectedRow();
        if (row < 0) {
            showWarning("Select a row to remove.");
            return;
        }
        batchQueue.remove(row);
        tableModel.removeRow(row);
        updateQueueCount();
        setStatus("Item removed from queue.", TEXT_MUTED);
    }

    private void onClearBatch(ActionEvent e) {
        if (batchQueue.isEmpty())
            return;
        int confirm = JOptionPane.showConfirmDialog(this,
                "Clear all " + batchQueue.size() + " queued item(s)?", "Confirm Clear",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            batchQueue.clear();
            tableModel.setRowCount(0);
            batchItemCounter = 1;
            updateQueueCount();
            setStatus("Batch queue cleared.", TEXT_MUTED);
        }
    }

    private BatchItem buildItemFromForm(String itemId) {
        CountryCode country = getSelectedCountry();
        DocumentType docType = getSelectedDocType();
        FileFormat format = getSelectedFormat();

        if (country == null || docType == null || format == null) {
            showWarning("Please select a country, document type, and format.");
            return null;
        }

        String path = txtFilePath.getText().trim();
        if (path.isBlank() || path.equals("No file selected...")) {
            showWarning("Please select a file using the Browse button.");
            return null;
        }

        File file = new File(path);

        try {
            FileFormat detected = FileFormat.fromFilename(file.getName());
            if (detected != format) {
                int choice = JOptionPane.showConfirmDialog(this,
                        String.format(
                                "The file extension '%s' doesn't match the selected format '%s'.\n"
                                        + "Proceed anyway with the selected format?",
                                detected.getExtension(), format.getExtension()),
                        "Format Mismatch", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (choice != JOptionPane.YES_OPTION)
                    return null;
            }
        } catch (IllegalArgumentException ex) {
            showWarning("Unrecognised file extension. Please select a supported file.");
            return null;
        }

        return new BatchItem("ITEM-" + itemId, country, docType, format, file);
    }

    /** Renders a BatchReport into the results panel */
    private void renderBatchReport(BatchReport report) {
        if (report.getSuccessCount() > 0) {
            for (ProcessingResult r : report.getSuccesses()) {
                appendResult("");
                appendResult(r.toDisplayString());
            }
        }
        if (report.hasFailures()) {
            appendResult("");
            appendResult("─── FAILURES =+======++=++===");
            for (FailureRecord f : report.getFailures()) {
                appendResult(f.toDisplayString());
            }
        }
        appendResult("");
        appendResult("  " + report.summary());
        lblResultsHeader.setText(report.summary());
    }

    private void appendResult(String text) {
        SwingUtilities.invokeLater(() -> {
            txtResults.append(text + "\n");
            txtResults.setCaretPosition(txtResults.getDocument().getLength());
        });
    }

    private void setStatus(String msg, Color color) {
        SwingUtilities.invokeLater(() -> {
            lblStatus.setText(msg);
            lblStatus.setForeground(color);
        });
    }

    private void updateQueueCount() {
        lblQueueCount.setText(
                batchQueue.size() + " item" + (batchQueue.size() == 1 ? "" : "s") + " queued");
        lblQueueCount.setForeground(batchQueue.isEmpty() ? TEXT_MUTED : ACCENT_HOVER);
    }

    private CountryCode getSelectedCountry() {
        return (CountryCode) cmbCountry.getSelectedItem();
    }

    private DocumentType getSelectedDocType() {
        return (DocumentType) cmbDocType.getSelectedItem();
    }

    private FileFormat getSelectedFormat() {
        return (FileFormat) cmbFormat.getSelectedItem();
    }

    private void showWarning(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    private String now() {
        return java.time.LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private JPanel createCard(String title) {
        JPanel card = new JPanel(new BorderLayout(0, 0));
        card.setBackground(BG_CARD);
        card.setBorder(new CompoundBorder(new LineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(14, 14, 14, 14)));

        JLabel lbl = new JLabel(title);
        lbl.setFont(FONT_TITLE);
        lbl.setForeground(TEXT_PRIMARY);
        lbl.setBorder(new EmptyBorder(0, 0, 10, 0));
        card.add(lbl, BorderLayout.NORTH);

        return card;
    }

    private <T> JComboBox<T> createCombo(T[] items) {
        JComboBox<T> combo = new JComboBox<>(items);
        styleInput(combo);
        return combo;
    }

    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField(placeholder);
        styleInput(field);
        return field;
    }

    private void styleInput(JComponent c) {
        c.setBackground(BG_INPUT);
        c.setForeground(TEXT_PRIMARY);
        c.setFont(FONT_LABEL);
        c.setBorder(new CompoundBorder(new LineBorder(BORDER_CLR, 1), new EmptyBorder(4, 8, 4, 8)));
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        if (c instanceof JComboBox<?> cb) {
            cb.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value,
                        int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected,
                            cellHasFocus);
                    setBackground(isSelected ? ACCENT : BG_INPUT);
                    setForeground(TEXT_PRIMARY);
                    setBorder(new EmptyBorder(4, 8, 4, 8));
                    return this;
                }
            });
        }
    }

    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(ACCENT_HOVER);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(ACCENT);
            }
        });
        return btn;
    }

    private JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(BG_INPUT);
        btn.setForeground(TEXT_PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorder(
                new CompoundBorder(new LineBorder(BORDER_CLR, 1), new EmptyBorder(6, 12, 6, 12)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(BG_PANEL);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(BG_INPUT);
            }
        });
        return btn;
    }

    private JButton createDangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(new Color(80, 20, 20));
        btn.setForeground(new Color(255, 130, 130));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(110, 30, 30));
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(80, 20, 20));
            }
        });
        return btn;
    }

    private void styleTable(JTable table) {
        table.setBackground(BG_INPUT);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(FONT_SMALL);
        table.setRowHeight(28);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(60, 60, 90));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.getTableHeader().setBackground(BG_PANEL);
        table.getTableHeader().setForeground(TEXT_MUTED);
        table.getTableHeader().setFont(FONT_SMALL);
        table.getTableHeader().setBorder(new MatteBorder(0, 0, 1, 0, BORDER_CLR));
        // Column widths
        int[] widths = {70, 55, 140, 60, 140};
        for (int i = 0; i < widths.length && i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    private JPanel sectionDivider(String label) {
        JPanel row = new JPanel(new BorderLayout(8, 0)) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, 20);
            }
        };
        row.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(99, 102, 241));
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_CLR);
        row.add(lbl, BorderLayout.WEST);
        row.add(sep, BorderLayout.CENTER);
        return row;
    }

}

