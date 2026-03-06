package model;

public enum CountryCode {

    CO("Colombia"), MX("Mexico"), AR("Argentina"), CL("Chile"), JP("Japan");

    private final String fullName;

    CountryCode(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    @Override
    public String toString() {
        return name() + " (" + fullName + ")";
    }
}
