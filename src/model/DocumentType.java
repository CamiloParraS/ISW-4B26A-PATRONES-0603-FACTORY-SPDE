package model;

public enum DocumentType {

    ELECTRONIC_INVOICE("Electronic Invoice"),
    LEGAL_CONTRACT("Legal Contract"),
    FINANCIAL_REPORT("Financial Report"),
    DIGITAL_CERTIFICATE("Digital Certificate"),
    TAX_DECLARATION("Tax Declaration");

    private final String displayName;

    DocumentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
