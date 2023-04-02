package ru.etysoft.religions.exceptions;

public class StructureException extends Exception {

    private String code;

    public StructureException(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
