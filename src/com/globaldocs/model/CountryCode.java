package com.globaldocs.model;

/**
 * Enum representing the four supported countries in the GlobalDocs system.
 * Each country code maps to a concrete DocumentProcessorFactory.
 */
public enum CountryCode {

    CO("Colombia"),
    MX("Mexico"),
    AR("Argentina"),
    CL("Chile");

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
