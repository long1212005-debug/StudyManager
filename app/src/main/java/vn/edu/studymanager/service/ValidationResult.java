package vn.edu.studymanager.service;

public class ValidationResult {
    private final boolean valid;
    private final String message;
    private final Float parsedValue;

    private ValidationResult(boolean valid, String message, Float parsedValue) {
        this.valid = valid;
        this.message = message;
        this.parsedValue = parsedValue;
    }

    public static ValidationResult success(float value) {
        return new ValidationResult(true, null, value);
    }

    public static ValidationResult success() {
        return new ValidationResult(true, null, null);
    }

    public static ValidationResult error(String message) {
        return new ValidationResult(false, message, null);
    }

    public boolean isValid() {
        return valid;
    }

    public String getMessage() {
        return message;
    }

    public Float getParsedValue() {
        return parsedValue;
    }
}
